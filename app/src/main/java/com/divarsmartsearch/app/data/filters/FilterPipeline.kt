package com.divarsmartsearch.app.data.filters

import com.divarsmartsearch.app.data.local.dao.BlockedPhoneDao
import com.divarsmartsearch.app.data.local.dao.ListingInteractionDao
import com.divarsmartsearch.app.data.local.entity.ListingEntity
import com.divarsmartsearch.app.data.local.entity.ListingInteractionEntity
import com.divarsmartsearch.app.data.local.entity.SavedSearchEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runs the full filter pipeline on a batch of listings, in order,
 * mirroring the original backend's apply_filters.py:
 *   1. Structured range filters (price/area/price-per-meter) from the SavedSearch.
 *   2. Permanent phone-number blocklist (official field + text-embedded numbers).
 *   3. Hard keyword exclusion ("مشاور"/"املاک" anywhere in the description).
 *   4. AI owner-detection (heuristic or LLM) for anything not already caught by step 3.
 *
 * Mutates each ListingEntity's isVisible/isLikelyAgency/ownerProbability
 * fields in place and returns the list that survived every stage.
 */
@Singleton
class FilterPipeline @Inject constructor(
    private val blockedPhoneDao: BlockedPhoneDao,
    private val listingInteractionDao: ListingInteractionDao,
) {
    suspend fun apply(
        savedSearch: SavedSearchEntity,
        listings: List<ListingEntity>,
        ownerDetectionThreshold: Double,
        anthropicApiKey: String?,
        anthropicModel: String,
    ): List<ListingEntity> {
        if (listings.isEmpty()) return emptyList()

        populateDetectedPhoneNumbers(listings)

        val rangeSurvivors = applyRangeFilters(savedSearch, listings)
        for (listing in listings) {
            if (!listing.isVisible) recordRejection(listing, "out_of_filter_range")
        }

        val blockedNumbers = blockedPhoneDao.getAllNumbers().toSet()
        val phoneSurvivors = rangeSurvivors.filter { listing ->
            val blocked = PhoneFilter.isBlocked(listing, blockedNumbers)
            if (blocked) {
                listing.isVisible = false
                recordRejection(listing, "blocked_phone")
            }
            !blocked
        }

        val finalKept = mutableListOf<ListingEntity>()
        for (listing in phoneSurvivors) {
            if (ForbiddenKeywords.containsForbiddenAgencyKeyword(listing.description)) {
                listing.isLikelyAgency = true
                listing.ownerProbability = 0.0
                listing.isVisible = false
                recordRejection(listing, "likely_agency")
                continue
            }

            val agencyProbability = OwnerDetector.agencyProbability(
                listing.description, anthropicApiKey, anthropicModel
            )
            listing.ownerProbability = 1.0 - agencyProbability
            listing.isLikelyAgency = agencyProbability >= ownerDetectionThreshold

            if (listing.isLikelyAgency) {
                listing.isVisible = false
                recordRejection(listing, "likely_agency")
            } else {
                finalKept.add(listing)
            }
        }

        return finalKept
    }

    private fun populateDetectedPhoneNumbers(listings: List<ListingEntity>) {
        for (listing in listings) {
            val numbers = PhoneExtraction.extractPhoneNumbers(listing.title, listing.description)
            listing.detectedPhoneNumbers = if (numbers.isNotEmpty()) numbers.joinToString(",") else null
        }
    }

    private fun applyRangeFilters(
        savedSearch: SavedSearchEntity,
        listings: List<ListingEntity>,
    ): List<ListingEntity> {
        for (listing in listings) {
            val price = listing.price
            val area = listing.area
            val pricePerMeter = listing.pricePerMeter
            val outOfRange = when {
                savedSearch.minPrice != null && price != null && price < savedSearch.minPrice -> true
                savedSearch.maxPrice != null && price != null && price > savedSearch.maxPrice -> true
                savedSearch.minArea != null && area != null && area < savedSearch.minArea -> true
                savedSearch.maxArea != null && area != null && area > savedSearch.maxArea -> true
                savedSearch.maxPricePerMeter != null && pricePerMeter != null &&
                    pricePerMeter > savedSearch.maxPricePerMeter -> true
                else -> false
            }
            if (outOfRange) listing.isVisible = false
        }
        return listings.filter { it.isVisible }
    }

    private suspend fun recordRejection(listing: ListingEntity, reason: String) {
        // Listing may not have a DB id yet if this is its first pass before
        // insertion; the repository re-associates interactions after insert.
        if (listing.id != 0L) {
            listingInteractionDao.insert(
                ListingInteractionEntity(listingId = listing.id, status = "rejected", rejectionReason = reason)
            )
        }
    }
}
