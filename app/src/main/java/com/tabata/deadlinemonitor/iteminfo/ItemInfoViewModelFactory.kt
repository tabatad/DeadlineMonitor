package com.tabata.deadlinemonitor.iteminfo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tabata.deadlinemonitor.database.ItemInfoDao

class ItemInfoViewModelFactory(
    private val dataSource: ItemInfoDao,
    private val application: Application): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemInfoViewModel::class.java)) {
            return ItemInfoViewModel(dataSource, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}