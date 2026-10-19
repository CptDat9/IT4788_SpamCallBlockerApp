package com.example.spamcallblocker_20235030.data

import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity
import com.example.spamcallblocker_20235030.data.relation.BrandWithNumbers
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import kotlinx.coroutines.flow.Flow

class SpamBlockerRepository(private val database: AppDatabase) {

    private val brandDao = database.brandDao()
    private val phoneDao = database.phoneNumberDao()
    private val logDao = database.blockedCallLogDao()

    // Brands
    val allBrandsFlow: Flow<List<BrandEntity>> = brandDao.getAllBrandsFlow()
    val allBrandsWithNumbersFlow: Flow<List<BrandWithNumbers>> = brandDao.getAllBrandsWithNumbersFlow()
    val totalBrandsCount: Flow<Int> = brandDao.getBrandCount()

    fun getBrandWithNumbersByIdFlow(id: Long): Flow<BrandWithNumbers?> =
        brandDao.getBrandWithNumbersByIdFlow(id)

    suspend fun getBrandById(id: Long): BrandEntity? = brandDao.getBrandById(id)

    suspend fun insertBrand(name: String, description: String = "", isEnabled: Boolean = true): Long {
        val trimmedName = name.trim()
        val brand = BrandEntity(name = trimmedName, description = description.trim(), isEnabled = isEnabled)
        return brandDao.insertBrand(brand)
    }

    suspend fun updateBrand(brand: BrandEntity) {
        brandDao.updateBrand(brand.copy(name = brand.name.trim(), description = brand.description.trim()))
    }

    suspend fun deleteBrand(brand: BrandEntity) {
        brandDao.deleteBrand(brand)
    }

    suspend fun setBrandEnabled(id: Long, isEnabled: Boolean) {
        brandDao.setBrandEnabled(id, isEnabled)
    }

    suspend fun isBrandNameExists(name: String, excludeId: Long = -1): Boolean {
        val trimmed = name.trim()
        return if (excludeId != -1L) {
            brandDao.checkBrandNameExistsExcluding(trimmed, excludeId)
        } else {
            brandDao.checkBrandNameExists(trimmed)
        }
    }

    // Phone Numbers
    val totalPhoneNumbersCount: Flow<Int> = phoneDao.getPhoneNumberCount()

    fun getNumbersForBrand(brandId: Long): Flow<List<PhoneNumberEntity>> =
        phoneDao.getNumbersForBrand(brandId)

    suspend fun addPhoneNumber(brandId: Long, rawNumber: String, note: String = ""): Long {
        val normalized = PhoneNumberUtils.normalize(rawNumber)
        val entity = PhoneNumberEntity(
            brandId = brandId,
            rawNumber = rawNumber.trim(),
            normalizedNumber = normalized,
            note = note.trim()
        )
        return phoneDao.insertNumber(entity)
    }

    suspend fun updatePhoneNumber(entity: PhoneNumberEntity, newRawNumber: String, newNote: String) {
        val normalized = PhoneNumberUtils.normalize(newRawNumber)
        val updated = entity.copy(
            rawNumber = newRawNumber.trim(),
            normalizedNumber = normalized,
            note = newNote.trim()
        )
        phoneDao.updateNumber(updated)
    }

    suspend fun deletePhoneNumber(entity: PhoneNumberEntity) {
        phoneDao.deleteNumber(entity)
    }

    suspend fun isNormalizedNumberExists(rawNumber: String, excludeId: Long = -1): Boolean {
        val normalized = PhoneNumberUtils.normalize(rawNumber)
        return if (excludeId != -1L) {
            phoneDao.checkNormalizedNumberExistsExcluding(normalized, excludeId)
        } else {
            phoneDao.checkNormalizedNumberExists(normalized)
        }
    }

    suspend fun findActiveBrandForNumber(rawNumber: String): String? {
        val normalized = PhoneNumberUtils.normalize(rawNumber)
        if (normalized.isEmpty()) return null
        return phoneDao.findActiveBrandForNumber(normalized)
    }

    // Blocked Call Logs
    val allLogsFlow: Flow<List<BlockedCallLogEntity>> = logDao.getAllLogsFlow()
    val totalBlockedCount: Flow<Int> = logDao.getBlockedCount()
    val recentLogsFlow: Flow<List<BlockedCallLogEntity>> = logDao.getRecentLogsFlow(5)

    suspend fun addBlockedLog(phoneNumber: String, brandName: String, reason: String): Long {
        val log = BlockedCallLogEntity(
            phoneNumber = phoneNumber,
            brandName = brandName,
            timestamp = System.currentTimeMillis(),
            reason = reason
        )
        return logDao.insertLog(log)
    }

    suspend fun clearAllLogs() {
        logDao.clearAllLogs()
    }

    suspend fun deleteLog(log: BlockedCallLogEntity) {
        logDao.deleteLog(log)
    }
}
