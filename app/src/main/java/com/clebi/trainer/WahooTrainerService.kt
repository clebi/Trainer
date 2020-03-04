package com.clebi.trainer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.clebi.trainer.model.Device
import com.clebi.trainer.model.DeviceType
import com.wahoofitness.connector.HardwareConnector
import com.wahoofitness.connector.HardwareConnector.Listener
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.HardwareConnectorTypes
import com.wahoofitness.connector.conn.connections.SensorConnection
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import com.wahoofitness.connector.listeners.discovery.DiscoveryListener

/**
 * DiscoveredListener provides interface to notify device discovery to consumer.
 */
typealias DiscoveredListener = (device: Device) -> Unit;

/**
 * WahooTrainerService communicates with Wahoo devices.
 */
class WahooTrainerService : Service() {

    companion object {
        lateinit var instance: WahooTrainerService
        val TAG = "WahooTrainerService"

        val sensorTypesToDeviceTypes = mapOf(
            HardwareConnectorTypes.SensorType.FITNESS_EQUIP to DeviceType.TRAINER,
            HardwareConnectorTypes.SensorType.BIKE_POWER to DeviceType.POWER
        )
    }

    class ServiceListener : Listener {
        override fun onHardwareConnectorStateChanged(
            p0: HardwareConnectorTypes.NetworkType,
            p1: HardwareConnectorEnums.HardwareConnectorState
        ) {
            Log.d(TAG, "network type: $p0")
        }

        override fun onFirmwareUpdateRequired(p0: SensorConnection, p1: String, p2: String) {
            Log.d(TAG, "update required!")
        }

    }

    class SearchListener(private val discoveredListeners: List<DiscoveredListener>) : DiscoveryListener {

        override fun onDiscoveredDeviceRssiChanged(params: ConnectionParams, p1: Int) {
            Log.d(TAG, "rssi changed: $params")
        }

        override fun onDiscoveredDeviceLost(params: ConnectionParams) {
            Log.d(TAG, "device lost: $params")
        }

        override fun onDeviceDiscovered(params: ConnectionParams) {
            Log.d(TAG, "discovered $params")
            val type = sensorTypesToDeviceTypes[params.sensorType] ?: return
            discoveredListeners.forEach {
                it(Device(params.id, params.antId, type, params.name))
            }
        }

    }

    private lateinit var hardwareConnector: HardwareConnector
    private val listeners = mutableListOf<DiscoveredListener>()
    private val searchListener = SearchListener(this.listeners)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        hardwareConnector = HardwareConnector(this, ServiceListener())
    }

    fun searchForDevices() {
        Log.d(TAG, "srart discovery")
        hardwareConnector.startDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    fun stopSearchForDevices() {
        Log.d(TAG, "stop discovery")
        hardwareConnector.stopDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    fun listenDevicesDiscovery(listener: DiscoveredListener) {
        listeners.add(listener)
    }
}
