package com.example.spamcallblocker_20235030.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "phone_numbers",
    foreignKeys = [
        ForeignKey(
            entity = BrandEntity::class,
            parentColumns = ["id"],
            childColumns = ["brandId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["brandId"]),
        Index(value = ["normalizedNumber"])
    ]
)
data class PhoneNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brandId: Long,
    val rawNumber: String,
    val normalizedNumber: String,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
