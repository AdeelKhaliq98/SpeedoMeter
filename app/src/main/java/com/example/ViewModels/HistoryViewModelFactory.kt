package com.example.ViewModels

import com.example.Repositeries.HistoryRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HistoryViewModelFactory(repository: HistoryRepository?) :
    ViewModelProvider.NewInstanceFactory() {

    private var mRepository: HistoryRepository? = null

    init {
        mRepository = repository
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryViewModel(mRepository!!) as T
    }
}