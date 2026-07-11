package com.divarsmartsearch.app.di

import android.content.Context
import androidx.room.Room
import com.divarsmartsearch.app.data.local.AppDatabase
import com.divarsmartsearch.app.data.local.dao.AppSettingsDao
import com.divarsmartsearch.app.data.local.dao.BlockedPhoneDao
import com.divarsmartsearch.app.data.local.dao.ListingDao
import com.divarsmartsearch.app.data.local.dao.ListingInteractionDao
import com.divarsmartsearch.app.data.local.dao.SavedSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Everything the app needs lives in a single on-device Room database —
 * there is no remote server/backend in this build (see README).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration() // fine for a personal, single-user local DB
            .build()

    @Provides
    fun provideSavedSearchDao(db: AppDatabase): SavedSearchDao = db.savedSearchDao()

    @Provides
    fun provideListingDao(db: AppDatabase): ListingDao = db.listingDao()

    @Provides
    fun provideBlockedPhoneDao(db: AppDatabase): BlockedPhoneDao = db.blockedPhoneDao()

    @Provides
    fun provideListingInteractionDao(db: AppDatabase): ListingInteractionDao = db.listingInteractionDao()

    @Provides
    fun provideAppSettingsDao(db: AppDatabase): AppSettingsDao = db.appSettingsDao()
}
