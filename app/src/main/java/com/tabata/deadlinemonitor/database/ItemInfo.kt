package com.tabata.deadlinemonitor.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "item_info_table")
data class ItemInfo(
    @PrimaryKey
    @ColumnInfo(name = "jan_code")
    val janCode: String = "",

    @ColumnInfo(name = "item_name")
    val itemName: String = "",

    @ColumnInfo(name = "deadline_date")
    val deadlineDate: Date? = Date(),

    @ColumnInfo(name = "check_cycle")
    val checkCycle: Int = -1,

    @ColumnInfo(name = "next_check_date")
    val nextCheckDate: Date? = Date(),

    @ColumnInfo(name = "is_checked")
    val isChecked: Int = 0
)