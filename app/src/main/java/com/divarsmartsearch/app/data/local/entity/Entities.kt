package com.divarsmartsearch.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "saved_searches")
data class SavedSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val searchUrl: String,
    val status: String, // "active" | "paused"
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minArea: Double? = null,
    val maxArea: Double? = null,
    val maxPricePerMeter: Double? = null,
    val city: String? = null,
    val neighborhood: String? = null,
    val propertyType: String? = null,
    val ownersOnly: Boolean = false,
    val maxListingAgeHours: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "listings",
    indices = [Index(value = ["savedSearchId", "divarToken"], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = SavedSearchEntity::class,
            parentColumns = ["id"],
            childColumns = ["savedSearchId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class ListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val savedSearchId: Long,
    val divarToken: String,
    val url: String,
    val title: String,
    var description: String? = null,
    var price: Double? = null,
    var area: Double? = null,
    var pricePerMeter: Double? = null,
    val neighborhood: String? = null,
    val city: String? = null,
    var contactPhone: String? = null,
    var detectedPhoneNumbers: String? = null, // comma-separated
    val publishedAt: Long? = null,
    val firstSeenAt: Long = System.currentTimeMillis(),
    var ownerProbability: Double? = null,
    var isLikelyAgency: Boolean = false,
    var isVisible: Boolean = true,
    var notified: Boolean = false,
)

@Entity(tableName = "blocked_phone_numbers")
data class BlockedPhoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val phoneNumber: String,
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "listing_interactions",
    foreignKeys = [
        ForeignKey(
            entity = ListingEntity::class,
            parentColumns = ["id"],
            childColumns = ["listingId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index(value = ["listingId"])],
)
data class ListingInteractionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val listingId: Long,
    val status: String, // "seen" | "saved" | "rejected"
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val darkModeEnabled: Boolean = false,
    val notificationSoundEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val notificationSoundUri: String = "default",
    val ownerDetectionThreshold: Double = 0.55,
    val anthropicApiKey: String? = null,
    val anthropicModel: String = "claude-haiku-4-5-20251001",
)
