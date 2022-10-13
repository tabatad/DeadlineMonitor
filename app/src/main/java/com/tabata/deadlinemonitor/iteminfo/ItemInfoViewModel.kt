package com.tabata.deadlinemonitor.iteminfo

import android.app.AlertDialog
import android.app.Application
import android.provider.SyncStateContract.Helpers.update
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

    val janCode = MutableLiveData<String>()
    val itemName = MutableLiveData<String>()
    var deadlineDate = Date()
    var checkCycle = -1
    var nextCheckDate: Calendar = Calendar.getInstance()

    private val _registerEvent = MutableLiveData<Boolean>()
    val registerEvent: LiveData<Boolean>
        get() = _registerEvent

    private val _isExist = MutableLiveData<Boolean>()
    val isExist: LiveData<Boolean>
        get() = _isExist

    private val _itemInfo = MutableLiveData<ItemInfo>()
    val itemInfo: LiveData<ItemInfo>
        get() = _itemInfo

    // 登録ボタンがタップされると実行
    fun onRegister() {
        viewModelScope.launch {
            nextCheckDate.time = deadlineDate
            nextCheckDate.add(Calendar.MONTH, -checkCycle)

            _itemInfo.value = ItemInfo(
                janCode.value.toString(),
                itemName.value.toString(),
                deadlineDate,
                checkCycle,
                nextCheckDate.time
            )
            if (_isExist.value == true) {
                // 重複するエンティティがあるとき
                update(_itemInfo.value!!)
            } else {
                // 重複するエンティティがないとき
                insert(_itemInfo.value!!)
            }
            _registerEvent.value = true
        }
    }

    fun isEntityDuplicate(janCode: String) {
        viewModelScope.launch {
            _itemInfo.value = getItemInfo(janCode)
            _isExist.value = _itemInfo.value != null
        }
    }

    fun delete(itemInfo: ItemInfo) {
        viewModelScope.launch {
            database.delete(itemInfo)
        }
    }

    private suspend fun insert(itemInfo: ItemInfo) {
        database.insert(itemInfo)
    }

    private suspend fun update(itemInfo: ItemInfo) {
        database.update(itemInfo)
    }

    private suspend fun getItemInfo(janCode: String): ItemInfo? {
        return database.getItemInfo(janCode)
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