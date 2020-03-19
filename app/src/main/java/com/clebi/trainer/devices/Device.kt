package com.clebi.trainer.devices

enum class DeviceType {
    TRAINER, POWER
}

data class Device(val id: String, val antId: Int, val type: DeviceType, val name: String, val params: Any)
