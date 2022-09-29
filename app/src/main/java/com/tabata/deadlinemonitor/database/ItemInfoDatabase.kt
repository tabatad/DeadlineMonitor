package com.tabata.deadlinemonitor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ItemInfo::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ItemInfoDatabase : RoomDatabase() {

    abstract val itemInfoDao: ItemInfoDao

    companion object {
        @Volatile
        private var INSTANCE: ItemInfoDatabase? = null

        fun getInstance(context: Context): ItemInfoDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ItemInfoDatabase::class.java,
                        "item_info_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}