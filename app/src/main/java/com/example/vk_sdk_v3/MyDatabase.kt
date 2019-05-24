package com.example.vk_sdk_v3

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Person::class, Common::class), version = 1)
abstract class MyDatabase2 : RoomDatabase() {
    abstract fun personDao(): PersonDao
    abstract fun commonDao(): CommonDao
}