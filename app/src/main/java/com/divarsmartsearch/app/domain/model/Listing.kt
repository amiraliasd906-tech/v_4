package com.divarsmartsearch.app.domain.model

data class Listing(
    val id: Int,
    val savedSearchId: Int,
    val url: String,
    val title: String,
    val description: String?,
    val price: Double?,
    val area: Double?,
    val pricePerMeter: Double?,
    val neighborhood: String?,
    val city: String?,
    val publishedAtEpochMillis: Long?,
    val firstSeenAtEpochMillis: Long,
    val ownerProbability: Double?,
    val isLikelyAgency: Boolean,
    val isVisible: Boolean,
    val detectedPhoneNumbers: List<String> = emptyList(),
)

enum class HistoryTab {
    SEEN,
    SAVED,
    REJECTED,
}
