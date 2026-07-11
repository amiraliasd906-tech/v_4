package com.divarsmartsearch.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.divarsmartsearch.app.data.local.entity.ListingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {

    @Query(
        "SELECT * FROM listings WHERE isVisible = 1 " +
            "AND (:savedSearchId IS NULL OR savedSearchId = :savedSearchId) " +
            "ORDER BY publishedAt DESC, firstSeenAt DESC"
    )
    fun observeVisible(savedSearchId: Long?): Flow<List<ListingEntity>>

    @Query(
        """
        SELECT listings.* FROM listings
        INNER JOIN listing_interactions ON listing_interactions.listingId = listings.id
        WHERE listing_interactions.status = :status
        GROUP BY listings.id
        ORDER BY listings.firstSeenAt DESC
        """
    )
    fun observeByInteractionStatus(status: String): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE savedSearchId = :savedSearchId")
    suspend fun getAllForSearch(savedSearchId: Long): List<ListingEntity>

    @Query("SELECT * FROM listings WHERE savedSearchId = :savedSearchId AND divarToken = :token LIMIT 1")
    suspend fun findByToken(savedSearchId: Long, token: String): ListingEntity?

    @Query("SELECT * FROM listings WHERE id = :id")
    suspend fun getById(id: Long): ListingEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: ListingEntity): Long

    @Update
    suspend fun update(entity: ListingEntity)
}
