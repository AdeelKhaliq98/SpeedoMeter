package com.example.ViewModels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.HistoryDatabase.HistoryEntityDataClass
import java.util.ArrayList

class ItemViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<Boolean>()
    private val mutableSelectedState = MutableLiveData<Boolean>()
    private val mutableTimeState=MutableLiveData<String>()
    private val mutableSelected=MutableLiveData<HistoryEntityDataClass>()

    val  timeObserver:LiveData<String> get()=mutableTimeState
    val    comingItem:LiveData<HistoryEntityDataClass> get() = mutableSelected
    val selectedItem: LiveData<Boolean> get() = mutableSelectedItem
    val selectedState: LiveData<Boolean> get() = mutableSelectedState

    fun fragmentToActivityButtonState(item: Boolean) {
        mutableSelectedItem.value = item
    }
    fun cominglocation(item: HistoryEntityDataClass){
        mutableSelected.value=item
    }
    fun FtoFButtonState(item: Boolean?)
    {
        mutableSelectedState.value=item
    }

    fun setTime(value:String)
    {
        mutableTimeState.value=value
    }

}