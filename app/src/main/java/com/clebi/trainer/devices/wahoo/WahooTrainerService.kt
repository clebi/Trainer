package com.clebi.trainer.devices.wahoo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.clebi.trainer.model.Device
import com.clebi.trainer.model.DeviceType
import com.clebi.trainer.model.NetworkState
import com.clebi.trainer.model.NetworkType
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
typealias DiscoveredListener = (device: Device) -> Unit
typealias NetworkChangeListener = (networkType: NetworkType, networkState: NetworkState) -> Unit

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

        val networkTypesConverter = mapOf(
            HardwareConnectorTypes.NetworkType.BTLE to NetworkType.BLUETOOTH
        )

        val networkStateConverter = mapOf(
            HardwareConnectorEnums.HardwareConnectorState.HARDWARE_READY to NetworkState.ENABLED,
            HardwareConnectorEnums.HardwareConnectorState.HARDWARE_NOT_ENABLED to NetworkState.DISABLED,
            HardwareConnectorEnums.HardwareConnectorState.HARDWARE_NOT_SUPPORTED to NetworkState.NOT_SUPPORTED
        )
    }

    /**
     * ServiceListener listen to wahoo service state changes.
     */
    class ServiceListener : Listener {
        /** listeners for network changes */
        private val listeners = mutableListOf<NetworkChangeListener>()

        /** The last known network state by network type */
        private val lastNetworkState = mutableMapOf(
            NetworkType.BLUETOOTH to NetworkState.NOT_SUPPORTED
        )

        override fun onHardwareConnectorStateChanged(
            networkType: HardwareConnectorTypes.NetworkType,
            networkState: HardwareConnectorEnums.HardwareConnectorState
        ) {
            Log.d(TAG, "network type: $networkType - $networkState")
            val networkType = networkTypesConverter[networkType] ?: return
            val networkState = networkStateConverter[networkState] ?: throw Error("unable to get network state")
            lastNetworkState[networkType] = networkState
            Log.d(TAG, "state listeners: ${listeners.count()}")
            listeners.forEach {
                it(networkType, networkState)
            }
        }

        override fun onFirmwareUpdateRequired(p0: SensorConnection, p1: String, p2: String) {
            Log.d(TAG, "update required!")
        }

        /**
         * Remove a network changes listener.
         * @param listener listener to add.
         */
        fun addListener(listener: NetworkChangeListener) {
            listeners.add(listener)
            for ((type, state) in lastNetworkState) {
                listener(type, state)
            }
        }

        /**
         * Remove a network changes listener.
         * @param listener listener to remove
         */
        fun removeListener(listener: NetworkChangeListener) {
            listeners.remove(listener)
        }
    }

    class SearchListener(
        private val discoveredListeners: List<DiscoveredListener>
    ) : DiscoveryListener {

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
                it(Device(params.id, params.antId, type, params.name, params))
            }
        }
    }

    private lateinit var hardwareConnector: HardwareConnector
    private val discoveredListeners = mutableListOf<DiscoveredListener>()
    private val searchListener =
        SearchListener(this.discoveredListeners)
    private val serviceListener = ServiceListener()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        hardwareConnector = HardwareConnector(this, serviceListener)
    }

    fun searchForDevices() {
        Log.d(TAG, "srart discovery")
        hardwareConnector.startDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    fun stopSearchForDevices() {
        Log.d(TAG, "stop discovery")
        hardwareConnector.stopDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    fun connectToDevice(device: Device): WahooConnectedDevice {
        val connectedDevice = WahooConnectedDevice(device)
        hardwareConnector.requestSensorConnection(device.params as ConnectionParams, connectedDevice)
        return connectedDevice
    }

    fun listenDevicesDiscovery(listener: DiscoveredListener) {
        discoveredListeners.add(listener)
    }

    fun unlistenDevicesDiscovery(listener: DiscoveredListener) {
        discoveredListeners.remove(listener)
    }

    /**
     * Listen to network changes.
     */
    fun listenNetworkChange(listener: NetworkChangeListener) {
        serviceListener.addListener(listener)
    }

    /**
     * Stop listening to network changes.
     */
    fun unlistenNetworkChange(listener: NetworkChangeListener) {
        serviceListener.removeListener(listener)
    }
}
