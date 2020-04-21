package com.clebi.trainer.devices.fake

import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.DeviceConnectionStatus
import com.clebi.trainer.devices.DeviceType
import com.clebi.trainer.devices.DiscoveredListener
import com.clebi.trainer.devices.NetworkChangeListener
import com.clebi.trainer.devices.NetworkState
import com.clebi.trainer.devices.NetworkType
import com.clebi.trainer.devices.TrainerApi
import java.util.Timer
import java.util.TimerTask

class FakeTrainerApi : TrainerApi {

    private val discoveryListeners = mutableListOf<DiscoveredListener>()
    private val networkChangeListener = mutableListOf<NetworkChangeListener>()

    override fun searchForDevices() {
        networkChangeListener.forEach {
            it(NetworkType.BLUETOOTH, NetworkState.ENABLED)
        }
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                discoveryListeners.forEach {
                    it(FakeDevice("fake_id", 123456, DeviceType.TRAINER, "fake_device", mapOf()))
                }
            }
        }, 3000)
    }

    override fun stopSearchForDevices() {
    }

    override fun connectToDevice(device: Device): ConnectedDevice {
        return FakeConnectedDevice(device, DeviceConnectionStatus.CONNECTED, listOf(DeviceCapability.BIKE_TRAINER))
    }

    override fun listenDevicesDiscovery(listener: DiscoveredListener) {
        discoveryListeners.add(listener)
    }

    override fun unlistenDevicesDiscovery(listener: DiscoveredListener) {
        discoveryListeners.remove(listener)
    }

    override fun listenNetworkChange(listener: NetworkChangeListener) {
        networkChangeListener.add(listener)
    }

    override fun unlistenNetworkChange(listener: NetworkChangeListener) {
        networkChangeListener.remove(listener)
    }

    override fun destroy() {
        discoveryListeners.clear()
        networkChangeListener.clear()
    }
}
