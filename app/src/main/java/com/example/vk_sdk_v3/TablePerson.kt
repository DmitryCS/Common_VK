package com.example.vk_sdk_v3

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.PrimaryKey

@Entity
data class Person(
    @PrimaryKey
    val vkId: Int,
    val name: String = "",
    val photo: String = "")