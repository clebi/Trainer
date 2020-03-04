package com.clebi.trainer.model

enum class DeviceType {
    TRAINER, POWER
}

data class Device(val id: String, val antId: Int, val type: DeviceType, val name: String)