package com.example.spamcallblocker_20235030.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.spamcallblocker_20235030.data.entity.BrandEntity
import com.example.spamcallblocker_20235030.data.entity.PhoneNumberEntity

data class BrandWithNumbers(
    @Embedded
    val brand: BrandEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "brandId"
    )
    val phoneNumbers: List<PhoneNumberEntity>
)
