package com.tabata.deadlinemonitor.database

import androidx.room.*

@Dao
interface ItemInfoDao {
    @Insert
    suspend fun insert(itemInfo: ItemInfo)

    @Update
    suspend fun update(itemInfo: ItemInfo)

    @Delete
    suspend fun delete(itemInfo: ItemInfo)

    @Query("DELETE FROM item_info_table")
    suspend fun clear()

    @Query("SELECT * FROM item_info_table WHERE jan_code = :janCode LIMIT 1")
    suspend fun getItemInfo(janCode: String): ItemInfo?

    @Query("SELECT * FROM item_info_table WHERE next_check_date < CAST((julianday('now') - 2440587.5)*86400000 AS INTEGER) ORDER BY deadline_date, next_check_date")
    suspend fun getCheckItemList(): List<ItemInfo>
}