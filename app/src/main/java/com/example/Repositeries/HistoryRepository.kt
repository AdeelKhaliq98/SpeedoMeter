package com.example.Repositeries

import com.example.HistoryDatabase.HistoryDao
import com.example.HistoryDatabase.HistoryEntityDataClass
import androidx.lifecycle.LiveData


class HistoryRepository(historyDao: HistoryDao?) {
    private var historyDao: HistoryDao? = historyDao

    companion object {

        private val LOCK = Any()
        private var sInstance: HistoryRepository? = null

        @Synchronized
        fun getInstance(
            historyDao: HistoryDao?,
        ): HistoryRepository? {
            if (sInstance == null) {
                synchronized(LOCK) {
                    sInstance = HistoryRepository(historyDao)
                }
            }
            return sInstance
        }
    }

     fun insertHistoryRecord(insert_History: HistoryEntityDataClass) {
        historyDao!!.insertHistory(insert_History)
    }

    fun getHistoryRecord(): LiveData<List<HistoryEntityDataClass>> {
        return historyDao!!.getHistoryRecord()
    }

    fun deleteHistory(local_id: HistoryEntityDataClass) {
        historyDao!!.deleteHistory(local_id)
    }

    fun loadTaskById(local_id: Int): LiveData<HistoryEntityDataClass?>? {
        return historyDao!!.loadTaskById(local_id)
    }
}