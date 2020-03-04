package com.clebi.trainer.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.model.Device

/**
 * ConfigModel holds information for the TrainerSearchFragment view.
 */
class ConfigModel : ViewModel() {
    private val _devices: MutableLiveData<List<Device>> = MutableLiveData(listOf())
    /** List of devices */
    val devices: LiveData<List<Device>> = _devices

    /**
     * Add device to the devices view.
     * @param device the device to add.
     */
    fun addDevice(device: Device) {
        val list = (_devices.value ?: arrayListOf()).toMutableList()
        list.add(device)
        _devices.apply {
            value = list
        }
    }
}