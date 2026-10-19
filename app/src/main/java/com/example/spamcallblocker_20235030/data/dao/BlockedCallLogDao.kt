package com.example.spamcallblocker_20235030.data.dao

import androidx.room.*
import com.example.spamcallblocker_20235030.data.entity.BlockedCallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallLogDao {

    @Query("SELECT * FROM blocked_call_logs ORDER BY timestamp DESC")
    fun getAllLogsFlow(): Flow<List<BlockedCallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BlockedCallLogEntity): Long

    @Query("DELETE FROM blocked_call_logs")
    suspend fun clearAllLogs(): Int

    @Delete
    suspend fun deleteLog(log: BlockedCallLogEntity): Int

    @Query("SELECT COUNT(*) FROM blocked_call_logs")
    fun getBlockedCount(): Flow<Int>

    @Query("SELECT * FROM blocked_call_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogsFlow(limit: Int): Flow<List<BlockedCallLogEntity>>
}
