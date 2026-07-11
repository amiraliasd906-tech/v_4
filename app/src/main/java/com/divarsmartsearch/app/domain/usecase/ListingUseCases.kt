package com.divarsmartsearch.app.domain.usecase

import com.divarsmartsearch.app.domain.model.HistoryTab
import com.divarsmartsearch.app.domain.model.Listing
import com.divarsmartsearch.app.domain.repository.ListingRepository
import com.divarsmartsearch.app.util.AppResult
import javax.inject.Inject

class GetVisibleListingsUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(searchId: Int? = null): AppResult<List<Listing>> =
        repository.getVisibleListings(searchId)
}

class GetListingHistoryUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(tab: HistoryTab): AppResult<List<Listing>> =
        repository.getHistory(tab)
}

class MarkListingSeenUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listingId: Int): AppResult<Unit> = repository.markSeen(listingId)
}

class SaveListingUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listingId: Int): AppResult<Unit> = repository.saveListing(listingId)
}

class RejectListingUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listingId: Int): AppResult<Unit> = repository.rejectListing(listingId)
}
