package com.divarsmartsearch.app.data.webview

import com.divarsmartsearch.app.data.filters.FilterPipeline
import com.divarsmartsearch.app.data.local.dao.AppSettingsDao
import com.divarsmartsearch.app.data.local.dao.ListingDao
import com.divarsmartsearch.app.data.local.dao.SavedSearchDao
import com.divarsmartsearch.app.data.local.entity.ListingEntity
import com.divarsmartsearch.app.notification.LocalNotifier
import javax.inject.Inject
import javax.inject.Singleton

data class IngestResult(val received: Int, val new: Int, val passedFilters: Int)

/**
 * Handles listings extracted by the in-app WebView. This is the
 * Kotlin/Room equivalent of the old backend's app/services/ingestion.py:
 * new listings are inserted, listings seen before are enriched in place
 * (e.g. a detail-page visit revealing a phone number), and each change
 * re-runs the full filter pipeline. Notifications are local and are
 * only ever sent once per listing (see `notified` on ListingEntity).
 */
@Singleton
class ListingIngestionService @Inject constructor(
    private val savedSearchDao: SavedSearchDao,
    private val listingDao: ListingDao,
    private val appSettingsDao: AppSettingsDao,
    private val filterPipeline: FilterPipeline,
    private val localNotifier: LocalNotifier,
) {
    suspend fun ingest(savedSearchId: Long, items: List<ExtractedListing>): IngestResult {
        val savedSearch = savedSearchDao.getById(savedSearchId) ?: return IngestResult(items.size, 0, 0)

        val brandNew = mutableListOf<ListingEntity>()
        val enriched = mutableListOf<ListingEntity>()

        for (item in items) {
            val existing = listingDao.findByToken(savedSearchId, item.divarToken)

            if (existing == null) {
                val entity = ListingEntity(
                    savedSearchId = savedSearchId,
                    divarToken = item.divarToken,
                    url = item.url,
                    title = item.title,
                    description = item.description,
                    price = item.price,
                    area = item.area,
                    pricePerMeter = item.pricePerMeter,
                    neighborhood = item.neighborhood ?: savedSearch.neighborhood,
                    city = savedSearch.city,
                    contactPhone = item.contactPhone,
                )
                val newId = listingDao.insert(entity)
                brandNew.add(entity.copy(id = newId))
            } else {
                var changed = false
                var updated = existing
                if (item.description != null && item.description != existing.description) {
                    updated = updated.copy(description = item.description); changed = true
                }
                if (item.price != null && item.price != existing.price) {
                    updated = updated.copy(price = item.price); changed = true
                }
                if (item.area != null && item.area != existing.area) {
                    updated = updated.copy(area = item.area); changed = true
                }
                if (item.pricePerMeter != null && item.pricePerMeter != existing.pricePerMeter) {
                    updated = updated.copy(pricePerMeter = item.pricePerMeter); changed = true
                }
                if (item.contactPhone != null && item.contactPhone != existing.contactPhone) {
                    updated = updated.copy(contactPhone = item.contactPhone); changed = true
                }
                if (changed) {
                    updated = updated.copy(isVisible = true, isLikelyAgency = false)
                    enriched.add(updated)
                }
            }
        }

        val toProcess = brandNew + enriched
        if (toProcess.isEmpty()) return IngestResult(items.size, 0, 0)

        val settings = appSettingsDao.get()
        val surviving = filterPipeline.apply(
            savedSearch = savedSearch,
            listings = toProcess,
            ownerDetectionThreshold = settings?.ownerDetectionThreshold ?: 0.55,
            anthropicApiKey = settings?.anthropicApiKey,
            anthropicModel = settings?.anthropicModel ?: "claude-haiku-4-5-20251001",
        )

        for (listing in toProcess) listingDao.update(listing)

        val toNotify = surviving.filter { !it.notified }
        for (listing in toNotify) {
            if (settings?.notificationsEnabled != false) {
                localNotifier.notifyNewListing(listing)
            }
            listingDao.update(listing.copy(notified = true))
        }

        return IngestResult(received = items.size, new = brandNew.size, passedFilters = surviving.size)
    }
}
