package com.example.ViewModels

import com.example.HistoryDatabase.HistoryEntityDataClass
import com.example.Repositeries.HistoryRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


open class HistoryViewModel(repository: HistoryRepository?) : ViewModel() {
    private var mRepository: HistoryRepository? = null

    init {
        mRepository = repository
    }

    fun getHistoryRecord(): LiveData<List<HistoryEntityDataClass>> {
        return mRepository!!.getHistoryRecord()
    }

    fun deleteHistory(local_id: HistoryEntityDataClass) {

        mRepository!!.deleteHistory(local_id)
    }


}