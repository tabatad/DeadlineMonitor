package com.tabata.deadlinemonitor.iteminfo

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tabata.deadlinemonitor.database.ItemInfo
import com.tabata.deadlinemonitor.database.ItemInfoDao
import kotlinx.coroutines.launch
import java.util.*

class ItemInfoViewModel(
    private val database: ItemInfoDao,
    application: Application
) : AndroidViewModel(application) {

    lateinit var itemInfo: ItemInfo
    val janCode = MutableLiveData<String>()
    val itemName = MutableLiveData<String>()
    var deadlineDate = Date()
    var checkCycle = -1

    var isInsertComplete = false

    private val _registerEvent = MutableLiveData<Boolean>()
    val registerEvent: LiveData<Boolean>
        get() = _registerEvent

    // 登録ボタンがタップされると実行
    fun onRegister() {
        viewModelScope.launch {
            itemInfo = ItemInfo(
                janCode.value.toString(),
                itemName.value.toString(),
                deadlineDate,
                checkCycle
            )
            insert(itemInfo)
            _registerEvent.value = true
        }
    }

    private suspend fun insert(itemInfo: ItemInfo) {
        try {
            database.insert(itemInfo)
            isInsertComplete = true
        } catch(e: SQLiteConstraintException) {
            Log.i("itemInfoViewModel", "$e")
        }
    }

    fun onClear() {
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        database.clear()
    }

    fun doneRegister() {
        _registerEvent.value = false
    }
}