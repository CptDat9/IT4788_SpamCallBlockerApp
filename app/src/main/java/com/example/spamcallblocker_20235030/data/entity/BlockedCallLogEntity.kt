package com.example.spamcallblocker_20235030.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_call_logs")
data class BlockedCallLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phoneNumber: String,
    val brandName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String = ""
)
