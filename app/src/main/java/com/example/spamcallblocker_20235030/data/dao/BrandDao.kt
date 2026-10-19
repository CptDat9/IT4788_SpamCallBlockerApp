package com.example.spamcallblocker_20235030.data.dao

import androidx.room.*
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.relation.BrandWithNumbers
import kotlinx.coroutines.flow.Flow

@Dao
interface BrandDao {

    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrandsFlow(): Flow<List<BrandEntity>>

    @Transaction
    @Query("SELECT * FROM brands ORDER BY name ASC")
    fun getAllBrandsWithNumbersFlow(): Flow<List<BrandWithNumbers>>

    @Query("SELECT * FROM brands WHERE id = :id")
    suspend fun getBrandById(id: Long): BrandEntity?

    @Transaction
    @Query("SELECT * FROM brands WHERE id = :id")
    fun getBrandWithNumbersByIdFlow(id: Long): Flow<BrandWithNumbers?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBrand(brand: BrandEntity): Long

    @Update
    suspend fun updateBrand(brand: BrandEntity): Int

    @Delete
    suspend fun deleteBrand(brand: BrandEntity): Int

    @Query("UPDATE brands SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setBrandEnabled(id: Long, isEnabled: Boolean): Int

    @Query("SELECT COUNT(*) FROM brands")
    fun getBrandCount(): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM brands WHERE LOWER(name) = LOWER(:name))")
    suspend fun checkBrandNameExists(name: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM brands WHERE LOWER(name) = LOWER(:name) AND id != :excludeId)")
    suspend fun checkBrandNameExistsExcluding(name: String, excludeId: Long): Boolean
}
