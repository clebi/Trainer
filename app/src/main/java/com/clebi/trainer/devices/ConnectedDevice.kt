package com.clebi.trainer.devices

/**
 * Callback for status change.
 */
typealias ConnectedDeviceListener = (device: ConnectedDevice) -> Unit

/**
 * DeviceConnectionStatus contains all possible connection status.
 */
enum class DeviceConnectionStatus {
    NOT_CONNECTED, CONNECTING, CONNECTED, DISCONNECTING;
}

/**
 * DeviceCapability lists all capabilities a device can have.
 */
enum class DeviceCapability {
    BIKE_TRAINER, BIKE_POWER;
}

/**
 * ConnectedDevice responsible for status of the connected device.
 */
interface ConnectedDevice {
    val device: Device
    var status: DeviceConnectionStatus
    var capabilities: List<DeviceCapability>

    fun addListener(listener: ConnectedDeviceListener)
    fun removeListener(listener: ConnectedDeviceListener)
    fun getBikeTrainer(): BikeTrainer
}
