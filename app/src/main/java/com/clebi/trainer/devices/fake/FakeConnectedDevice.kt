package com.clebi.trainer.devices.fake

import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectedDeviceListener
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.DeviceConnectionStatus

class FakeConnectedDevice(
    override val device: Device,
    override var status: DeviceConnectionStatus,
    override var capabilities: List<DeviceCapability>
) : ConnectedDevice {
    private val listeners = mutableListOf<ConnectedDeviceListener>()
    override fun addListener(listener: ConnectedDeviceListener) {
        listeners.add(listener)
        listener(this)
    }

    override fun getBikeTrainer(): BikeTrainer {
        return FakeBikeTrainer()
    }

    override fun removeListener(listener: ConnectedDeviceListener) {
        listeners.remove(listener)
    }
}
