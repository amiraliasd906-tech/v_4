package com.divarsmartsearch.app.domain.repository

import com.divarsmartsearch.app.domain.model.HistoryTab
import com.divarsmartsearch.app.domain.model.Listing
import com.divarsmartsearch.app.util.AppResult

interface ListingRepository {
    suspend fun getVisibleListings(searchId: Int? = null): AppResult<List<Listing>>
    suspend fun getHistory(tab: HistoryTab): AppResult<List<Listing>>
    suspend fun markSeen(listingId: Int): AppResult<Unit>
    suspend fun saveListing(listingId: Int): AppResult<Unit>
    suspend fun rejectListing(listingId: Int): AppResult<Unit>
}
