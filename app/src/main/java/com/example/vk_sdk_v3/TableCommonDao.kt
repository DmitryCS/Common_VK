package com.example.vk_sdk_v3

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface CommonDao{

    @Query("SELECT * FROM com ORDER BY uid DESC")
    fun getAll(): List<Common>

    @Query("SELECT * FROM com where uid == :id")
    fun getObject(id: Int): Common

    @Query("SELECT uid FROM com WHERE vkId1 == :id1 and vkId2 == :id2" )
    fun getIdByVkids(id1: Int, id2: Int): Int

    @Insert
    fun insert(com: Common)

    @Delete
    fun deleteCommon(com: Common)

    @Query("DELETE FROM com")
    fun deleteAll()
}