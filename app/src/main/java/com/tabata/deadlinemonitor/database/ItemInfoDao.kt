package com.tabata.deadlinemonitor.database

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface ItemInfoDao {
    @Insert
    suspend fun insert(itemInfo: ItemInfo)
}