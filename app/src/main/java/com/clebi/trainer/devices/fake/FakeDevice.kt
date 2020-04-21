package com.clebi.trainer.devices.fake

import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceType
import org.json.JSONObject

class FakeDevice(
    override val id: String,
    override val antId: Int,
    override val type: DeviceType,
    override val name: String,
    override val params: Map<String, String>
) : Device {
    companion object {
        const val PROVIDER = "fake"
    }

    override val provider = PROVIDER

    override fun jsonSerialize(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("antId", antId)
        json.put("name", name)
        json.put("type", type)
        json.put("provider", provider)
        json.put("params", params)
        return json
    }
}
