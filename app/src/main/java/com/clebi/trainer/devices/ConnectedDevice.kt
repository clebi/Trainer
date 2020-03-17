package com.clebi.trainer.devices

import com.clebi.trainer.model.Device

/**
 * Callback for status change.
 */
typealias ConnectionStatusCallback = (status: DeviceConnectionStatus) -> Unit

/**
 * DeviceConnectionStatus contains all possible connection status.
 */
enum class DeviceConnectionStatus {
    NOT_CONNECTED, CONNECTING, CONNECTED, DISCONNECTING;
}

/**
 * ConnectedDevice responsible for status of the connected device.
 */
interface ConnectedDevice {
    val device: Device
    var status: DeviceConnectionStatus

    fun addConnectionStatusListeners(listener: ConnectionStatusCallback)
    fun removeConnectionStatusListeners(listener: ConnectionStatusCallback)
}
