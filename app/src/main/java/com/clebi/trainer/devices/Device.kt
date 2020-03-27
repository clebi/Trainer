package com.clebi.trainer.devices

import org.json.JSONObject

/**
 * DeviceType is the device type.
 */
enum class DeviceType {
    TRAINER, POWER
}

/**
 * Device is interface for device.
 */
interface Device {

    val id: String
    val antId: Int
    val type: DeviceType
    val name: String
    val params: Any
    val provider: String

    fun jsonSerialize(): JSONObject
}
