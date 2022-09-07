package com.example.speedometer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.HistoryDatabase.HistoryEntityDataClass
import com.example.Repositeries.HistoryRepository

class HistoryAddTaskViewModel(repository: HistoryRepository?) : ViewModel() {
    private var mRepository: HistoryRepository? = null

    init {
        mRepository = repository
    }

    fun getHistory(local_id: Int): LiveData<HistoryEntityDataClass?>? {
        return mRepository!!.loadTaskById(local_id)
    }

     fun insertHistory(insert_History: HistoryEntityDataClass) {
           mRepository!!.insertHistoryRecord(insert_History)

    }

}
