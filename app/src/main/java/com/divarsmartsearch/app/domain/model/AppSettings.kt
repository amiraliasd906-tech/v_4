package com.divarsmartsearch.app.domain.model

data class BlockedPhoneNumber(
    val id: Int,
    val phoneNumber: String,
    val note: String?,
)

data class AppSettings(
    val darkModeEnabled: Boolean,
    val notificationSoundEnabled: Boolean,
    val notificationsEnabled: Boolean,
    val notificationSoundUri: String,
    val ownerDetectionThreshold: Double = 0.55,
    val anthropicApiKey: String? = null,
    val anthropicModel: String = "claude-haiku-4-5-20251001",
)
