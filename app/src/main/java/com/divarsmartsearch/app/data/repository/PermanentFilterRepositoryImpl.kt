package com.divarsmartsearch.app.data.repository

import com.divarsmartsearch.app.data.filters.PhoneFilter
import com.divarsmartsearch.app.data.local.dao.BlockedPhoneDao
import com.divarsmartsearch.app.data.local.entity.BlockedPhoneEntity
import com.divarsmartsearch.app.data.local.toDomain
import com.divarsmartsearch.app.domain.model.BlockedPhoneNumber
import com.divarsmartsearch.app.domain.repository.PermanentFilterRepository
import com.divarsmartsearch.app.util.AppResult
import com.divarsmartsearch.app.util.safeCall
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermanentFilterRepositoryImpl @Inject constructor(
    private val dao: BlockedPhoneDao,
) : PermanentFilterRepository {

    override suspend fun getBlockedNumbers(): AppResult<List<BlockedPhoneNumber>> = safeCall {
        dao.observeAll().first().map { it.toDomain() }
    }

    override suspend fun addBlockedNumber(phoneNumber: String, note: String?): AppResult<BlockedPhoneNumber> = safeCall {
        val normalized = PhoneFilter.normalizePhone(phoneNumber)
        if (normalized.length < 10) throw IllegalArgumentException("شماره تلفن معتبر نیست")

        val existing = dao.findByNumber(normalized)
        if (existing != null) throw IllegalStateException("این شماره قبلاً در لیست فیلتر دائمی وجود دارد")

        val entity = BlockedPhoneEntity(phoneNumber = normalized, note = note)
        val id = dao.insert(entity)
        entity.copy(id = id).toDomain()
    }

    override suspend fun removeBlockedNumber(id: Int): AppResult<Unit> = safeCall {
        dao.deleteById(id.toLong())
    }
}
