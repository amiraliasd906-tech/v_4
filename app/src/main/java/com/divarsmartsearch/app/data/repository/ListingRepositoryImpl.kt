package com.divarsmartsearch.app.data.repository

import com.divarsmartsearch.app.data.local.dao.ListingDao
import com.divarsmartsearch.app.data.local.dao.ListingInteractionDao
import com.divarsmartsearch.app.data.local.entity.ListingInteractionEntity
import com.divarsmartsearch.app.data.local.toDomain
import com.divarsmartsearch.app.domain.model.HistoryTab
import com.divarsmartsearch.app.domain.model.Listing
import com.divarsmartsearch.app.domain.repository.ListingRepository
import com.divarsmartsearch.app.util.AppResult
import com.divarsmartsearch.app.util.safeCall
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ListingRepositoryImpl @Inject constructor(
    private val listingDao: ListingDao,
    private val interactionDao: ListingInteractionDao,
) : ListingRepository {

    override suspend fun getVisibleListings(searchId: Int?): AppResult<List<Listing>> = safeCall {
        listingDao.observeVisible(searchId?.toLong()).first().map { it.toDomain() }
    }

    override suspend fun getHistory(tab: HistoryTab): AppResult<List<Listing>> = safeCall {
        val status = when (tab) {
            HistoryTab.SEEN -> "seen"
            HistoryTab.SAVED -> "saved"
            HistoryTab.REJECTED -> "rejected"
        }
        listingDao.observeByInteractionStatus(status).first().map { it.toDomain() }
    }

    override suspend fun markSeen(listingId: Int): AppResult<Unit> = safeCall {
        interactionDao.insert(ListingInteractionEntity(listingId = listingId.toLong(), status = "seen"))
    }

    override suspend fun saveListing(listingId: Int): AppResult<Unit> = safeCall {
        interactionDao.insert(ListingInteractionEntity(listingId = listingId.toLong(), status = "saved"))
    }

    override suspend fun rejectListing(listingId: Int): AppResult<Unit> = safeCall {
        interactionDao.insert(
            ListingInteractionEntity(
                listingId = listingId.toLong(),
                status = "rejected",
                rejectionReason = "user_rejected",
            )
        )
        val listing = listingDao.getById(listingId.toLong())
        if (listing != null) {
            listingDao.update(listing.copy(isVisible = false))
        }
    }
}
