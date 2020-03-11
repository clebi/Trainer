package com.clebi.trainer.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.model.Device

/**
 * ConfigModel holds information for the TrainerSearchFragment view.
 */
class DevicesConfigModel : ViewModel() {
    private val _searchDevices: MutableLiveData<List<Device>> = MutableLiveData(listOf())

    /** List of search devices */
    val searchDevices: LiveData<List<Device>> = _searchDevices

    fun resetSearchDevices() {
        _searchDevices.apply {
            value = listOf()
        }
    }

    /**
     * Add device to the devices view.
     * @param device the device to add.
     */
    fun addSearchDevice(device: Device) {
        val list = (_searchDevices.value ?: arrayListOf()).toMutableList()
        list.add(device)
        _searchDevices.apply {
            value = list
        }
    }

    private val _connectedDevices: MutableLiveData<List<ConnectedDevice>> = MutableLiveData(listOf())
    val connectedDevices: LiveData<List<ConnectedDevice>> = _connectedDevices

    fun addConnectedDevices(device: ConnectedDevice) {
        val list = (_connectedDevices.value ?: listOf()).toMutableList()
        list.add(device)
        _connectedDevices.apply {
            value = list
        }
    }
}
