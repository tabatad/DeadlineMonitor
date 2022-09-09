package com.tabata.deadlinemonitor.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemInfoDao {
    @Insert
    suspend fun insert(itemInfo: ItemInfo)

    @Update
    suspend fun update(itemInfo: ItemInfo)

    @Query("DELETE FROM item_info_table")
    suspend fun clear()

    @Query("SELECT * FROM item_info_table WHERE jan_code = :janCode LIMIT 1")
    suspend fun getItemInfo(janCode: String): ItemInfo?
}