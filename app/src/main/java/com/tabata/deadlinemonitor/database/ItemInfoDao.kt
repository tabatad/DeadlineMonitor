package com.tabata.deadlinemonitor.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemInfoDao {
    @Insert
    suspend fun insert(itemInfo: ItemInfo)

    @Query("DELETE FROM item_info_table")
    suspend fun clear()
}