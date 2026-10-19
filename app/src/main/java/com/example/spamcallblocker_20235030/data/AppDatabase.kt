package com.example.spamcallblocker_20235030.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.spamcallblocker_20235030.data.dao.BlockedCallLogDao
import com.example.spamcallblocker_20235030.data.dao.BrandDao
import com.example.spamcallblocker_20235030.data.dao.PhoneNumberDao
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity
import com.example.spamcallblocker_20235030.util.PhoneNumberUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BrandEntity::class,
        PhoneNumberEntity::class,
        BlockedCallLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun brandDao(): BrandDao
    abstract fun phoneNumberDao(): PhoneNumberDao
    abstract fun blockedCallLogDao(): BlockedCallLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "spam_call_blocker_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                val brandDao = database.brandDao()
                val phoneDao = database.phoneNumberDao()
                val logDao = database.blockedCallLogDao()

                // Thương hiệu 1: Tài Chính & Vay Tiền Spam
                val b1Id = brandDao.insertBrand(
                    BrandEntity(
                        name = "Tài Chính & Vay Tiền Spam",
                        description = "Gọi điện chào mời vay tín chấp không rõ nguồn gốc",
                        isEnabled = true
                    )
                )
                val numbers1 = listOf("0901234567", "0918765432", "02873000123")
                numbers1.forEach { num ->
                    phoneDao.insertNumber(
                        PhoneNumberEntity(
                            brandId = b1Id,
                            rawNumber = num,
                            normalizedNumber = PhoneNumberUtils.normalize(num),
                            note = "Số gọi tự động (Robocall)"
                        )
                    )
                }

                // Thương hiệu 2: Bất Động Sản Chào Mời
                val b2Id = brandDao.insertBrand(
                    BrandEntity(
                        name = "Bất Động Sản Chào Mời",
                        description = "Spam dự án chung cư và đất nền",
                        isEnabled = true
                    )
                )
                val numbers2 = listOf("0938111222", "0909333444")
                numbers2.forEach { num ->
                    phoneDao.insertNumber(
                        PhoneNumberEntity(
                            brandId = b2Id,
                            rawNumber = num,
                            normalizedNumber = PhoneNumberUtils.normalize(num),
                            note = "Tư vấn dự án đất nền"
                        )
                    )
                }

                // Thương hiệu 3: Lừa Đảo Chứng Khoán Quốc Tế
                val b3Id = brandDao.insertBrand(
                    BrandEntity(
                        name = "Lừa Đảo Chứng Khoán Quốc Tế",
                        description = "Dụ dỗ tham gia sàn giao dịch ảo",
                        isEnabled = true
                    )
                )
                val numbers3 = listOf("0945666777", "02439998888")
                numbers3.forEach { num ->
                    phoneDao.insertNumber(
                        PhoneNumberEntity(
                            brandId = b3Id,
                            rawNumber = num,
                            normalizedNumber = PhoneNumberUtils.normalize(num),
                            note = "Dụ dỗ nạp tiền sàn FX"
                        )
                    )
                }

                // Thêm lịch sử mẫu để giao diện có dữ liệu thực tế minh họa
                val now = System.currentTimeMillis()
                logDao.insertLog(
                    BlockedCallLogEntity(
                        phoneNumber = "0901234567",
                        brandName = "Tài Chính & Vay Tiền Spam",
                        timestamp = now - 3600000,
                        reason = "Cuộc gọi bị từ chối tự động theo danh sách thương hiệu"
                    )
                )
                logDao.insertLog(
                    BlockedCallLogEntity(
                        phoneNumber = "0938111222",
                        brandName = "Bất Động Sản Chào Mời",
                        timestamp = now - 86400000,
                        reason = "Cuộc gọi bị từ chối tự động theo danh sách thương hiệu"
                    )
                )
            }
        }
    }
}
