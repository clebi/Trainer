package com.clebi.trainer.devices.wahoo

import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceType
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import org.json.JSONObject

/**
 * WahooDevice is the implementation  of device for wahoo api.
 */
data class WahooDevice(
    override val id: String,
    override val antId: Int,
    override val type: DeviceType,
    override val name: String,
    override val params: ConnectionParams
) : Device {
    override val provider = "wahoo"

    override fun jsonSerialize(): JSONObject {
        val json = JSONObject()
        json.put("id", id)
        json.put("antId", antId)
        json.put("name", name)
        json.put("type", type)
        json.put("provider", provider)
        json.put("params", params.serialize())
        return json
    }
}
