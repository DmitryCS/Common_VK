package com.example.vk_sdk_v3

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity
    (tableName = "com", foreignKeys = arrayOf(
    ForeignKey(entity = Person::class,
        parentColumns = arrayOf("vkId"),
        childColumns = arrayOf("vkId1"),
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(entity = Person::class,
        parentColumns = arrayOf("vkId"),
        childColumns = arrayOf("vkId2"),
        onDelete = ForeignKey.CASCADE
)
))
data class Common(
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    val vkId1: Int,
    val vkId2: Int,
    val common: Int,
    val ForG: Int
)