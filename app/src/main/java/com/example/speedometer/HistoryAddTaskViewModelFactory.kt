package com.example.speedometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.Repositeries.HistoryRepository

class HistoryAddTaskViewModelFactory(repository: HistoryRepository?) :
    ViewModelProvider.NewInstanceFactory() {

    private var mRepository: HistoryRepository? = null

    init {
        mRepository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryAddTaskViewModel(mRepository!!) as T
    }
}