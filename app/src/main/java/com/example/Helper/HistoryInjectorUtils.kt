package com.example.Helper

import com.example.HistoryDatabase.HistoryDatabase
import com.example.Repositeries.HistoryRepository
import com.example.ViewModels.HistoryViewModelFactory
import android.content.Context
import com.example.ViewModels.HistoryViewModel
import com.example.speedometer.HistoryAddTaskViewModelFactory


fun Context.provideHistoryRepository(): HistoryRepository? {
    val database = HistoryDatabase.getInstance(applicationContext)
    return HistoryRepository.getInstance(database!!.historyDao())
}

fun Context.provideHistoryAddTaskViewModelFactory(): HistoryAddTaskViewModelFactory {
    return HistoryAddTaskViewModelFactory(provideHistoryRepository()!!)
}

fun Context.provideHistoryViewModelFactory(): HistoryViewModelFactory {
    return HistoryViewModelFactory(provideHistoryRepository()!!)
}
