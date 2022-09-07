package com.example.HistoryDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntityDataClass::class], version = 2, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao?

    companion object {
        private val LOCK = Any()
        private const val DATABASE_NAME = "HistoryRecordDB"
        private var sInstance: HistoryDatabase? = null
        fun getInstance(context: Context): HistoryDatabase? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDatabase::class.java, DATABASE_NAME
                    )
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return sInstance
        }
    }
}