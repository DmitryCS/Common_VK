package com.example.vk_sdk_v3

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface PersonDao{

    @Query("SELECT * FROM person")
    fun getAllPeople(): List<Person>

    @Query("SELECT name FROM person WHERE vkId == :name")
    fun getNameByVkid(name: Int): String

    @Query("SELECT * FROM person WHERE vkId == :id")
    fun getObjByVkid(id: Int): Person

    @Insert
    fun insert(person: Person)

    @Query("DELETE FROM person")
    fun deleteAll()

    @Delete
    fun deleteGender(person: Person)
}