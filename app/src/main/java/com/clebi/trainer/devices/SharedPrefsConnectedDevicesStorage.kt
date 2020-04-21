package com.clebi.trainer.devices

import android.content.Context
import android.util.Log
import com.clebi.trainer.devices.fake.FakeDevice
import com.clebi.trainer.devices.wahoo.WahooDevice
import com.clebi.trainer.trainings.FileTrainingStorage
import com.clebi.trainer.trainings.TrainingsStorage
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import org.json.JSONArray
import org.json.JSONObject

class SharedPrefsConnectedDevicesStorage(context: Context) : ConnectedDevicesStorage {
    companion object {
        private const val TAG = "SharedPrefsConnectedDevicesStorage"

        private const val PREFS_KEY = "devices"

        /** storage key for trainings */
        private const val TRAININGS_KEY = "devices_list_v1"

        private const val STORAGE_TYPE = "shared_preferences"
    }

    private val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    /** @see TrainingsStorage.storageType */
    override val storageType: String = FileTrainingStorage.STORAGE_TYPE
    override fun isAccessible(): Boolean {
        return true
    }

    override fun read(): List<Device> {
        val json = prefs.getString(TRAININGS_KEY, "[]")
        Log.d(TAG, "read json: $json")
        val objs = JSONArray(json)
        val devices = mutableListOf<Device>()
        for (i in 0 until objs.length()) {
            val obj = objs.get(i) as JSONObject
            val deviceType = when (obj.get("type")) {
                "TRAINER" -> DeviceType.TRAINER
                "POWER" -> DeviceType.POWER
                else -> null
            }
            val provider = obj.getString("provider")
            val device = when (provider) {
                FakeDevice.PROVIDER -> FakeDevice(
                    obj.getString("id"),
                    obj.getInt("antId"),
                    deviceType!!,
                    obj.getString("name"),
                    mapOf()
                )
                WahooDevice.PROVIDER -> WahooDevice(
                    obj.getString("id"),
                    obj.getInt("antId"),
                    deviceType!!,
                    obj.getString("name"),
                    ConnectionParams.deserialize(obj.getString("params"))!!
                )
                else -> {
                    Log.w(TAG, "unable to load device for provider: $provider")
                    null
                }
            }
            device?.let { devices.add(it) }
        }
        Log.d(TAG, "devices: $devices")
        return devices
    }

    override fun write(saveTime: Long, devices: List<ConnectedDevice>) {
        val json = JSONArray()
        devices.forEach {
            json.put(it.device.jsonSerialize())
        }
        Log.d(TAG, "write json: $json")
        val editor = prefs.edit()
        editor.putString(TRAININGS_KEY, json.toString())
        editor.apply()
    }
}
