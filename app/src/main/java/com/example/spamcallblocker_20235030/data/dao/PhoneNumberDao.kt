package com.example.spamcallblocker_20235030.data.dao

import androidx.room.*
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity
import com.example.spamcallblocker_20235030.data.relation.ActivePhoneMapping
import kotlinx.coroutines.flow.Flow

@Dao
interface PhoneNumberDao {

    @Query("SELECT * FROM phone_numbers WHERE brandId = :brandId ORDER BY createdAt DESC")
    fun getNumbersForBrand(brandId: Long): Flow<List<PhoneNumberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNumber(number: PhoneNumberEntity): Long

    @Update
    suspend fun updateNumber(number: PhoneNumberEntity): Int

    @Delete
    suspend fun deleteNumber(number: PhoneNumberEntity): Int

    @Query("DELETE FROM phone_numbers WHERE id = :id")
    suspend fun deleteNumberById(id: Long): Int

    @Query("""
        SELECT p.normalizedNumber AS normalizedNumber, b.name AS brandName 
        FROM phone_numbers p 
        INNER JOIN brands b ON p.brandId = b.id 
        WHERE b.isEnabled = 1
    """)
    suspend fun getAllNormalizedNumbersForActiveBrands(): List<ActivePhoneMapping>

    @Query("""
        SELECT b.name AS brandName 
        FROM phone_numbers p 
        INNER JOIN brands b ON p.brandId = b.id 
        WHERE p.normalizedNumber = :normalizedNumber AND b.isEnabled = 1
        LIMIT 1
    """)
    suspend fun findActiveBrandForNumber(normalizedNumber: String): String?

    @Query("SELECT COUNT(*) FROM phone_numbers")
    fun getPhoneNumberCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM phone_numbers WHERE normalizedNumber = :normalizedNumber)")
    suspend fun checkNormalizedNumberExists(normalizedNumber: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM phone_numbers WHERE normalizedNumber = :normalizedNumber AND id != :excludeId)")
    suspend fun checkNormalizedNumberExistsExcluding(normalizedNumber: String, excludeId: Long): Boolean
}
