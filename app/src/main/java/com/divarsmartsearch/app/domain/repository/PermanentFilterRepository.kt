package com.divarsmartsearch.app.domain.repository

import com.divarsmartsearch.app.domain.model.BlockedPhoneNumber
import com.divarsmartsearch.app.util.AppResult

interface PermanentFilterRepository {
    suspend fun getBlockedNumbers(): AppResult<List<BlockedPhoneNumber>>
    suspend fun addBlockedNumber(phoneNumber: String, note: String?): AppResult<BlockedPhoneNumber>
    suspend fun removeBlockedNumber(id: Int): AppResult<Unit>
}
