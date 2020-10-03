package com.clebi.trainer.ui.config

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectedDevicesStorage
import com.clebi.trainer.devices.Device

/**
 * ConfigModel holds information for the TrainerSearchFragment view.
 */
class DevicesConfigModel(private val devicesStorages: Array<ConnectedDevicesStorage>) : ViewModel() {
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
        _searchDevices.postValue(list)
    }

    private val _connectedDevices: MutableLiveData<List<ConnectedDevice>> = MutableLiveData(listOf())
    val connectedDevices: LiveData<List<ConnectedDevice>> = _connectedDevices

    /**
     * Read the list of devices from storage.
     */
    fun readConnectedDevicesFromStorage(): List<Device> {
        val trainingsContainer = devicesStorages.mapNotNull { item ->
            if (item.isAccessible()) {
                item.read()
            } else {
                null
            }
        }
        val devices = mutableListOf<Device>()
        trainingsContainer[0].forEach { device ->
            if (_connectedDevices.value!!.filter { it.device.id == device.id }.count() < 1) {
                devices.add(device)
            }
        }
        return devices
    }

    /**
     * Save connected devices to storage.
     */
    fun saveConnectedDevicesToStorage() {
        val epoch = System.currentTimeMillis() / 1000
        val trainings = (_connectedDevices.value ?: listOf())
        devicesStorages.forEach {
            if (it.isAccessible()) {
                it.write(epoch, trainings)
            }
        }
    }

    fun addConnectedDevices(device: ConnectedDevice) {
        val list = (_connectedDevices.value ?: listOf()).toMutableList()
        if (list.filter { it.device.id == device.device.id }.count() > 0) {
            return
        }
        list.add(device)
        _connectedDevices.apply {
            value = list
        }
    }
}
