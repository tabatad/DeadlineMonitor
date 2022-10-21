package com.tabata.deadlinemonitor.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long {
        val day = 1000 * 60 * 60 * 24
        return date!!.time - (date.time % day)
    }
}