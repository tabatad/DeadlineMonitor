package com.tabata.deadlinemonitor.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tabata.deadlinemonitor.database.ItemInfo
import com.tabata.deadlinemonitor.database.ItemInfoDao
import kotlinx.coroutines.launch

class HomeViewModel(
    private val database: ItemInfoDao,
    application: Application
) : AndroidViewModel(application) {

    private val _dataSet = MutableLiveData<List<ItemInfo>>()
    val dataSet: LiveData<List<ItemInfo>>
        get() = _dataSet

    fun updateCheckItemList() {
        viewModelScope.launch {
            _dataSet.value = database.getCheckItemList()
        }
    }
}