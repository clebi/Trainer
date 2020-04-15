package com.clebi.trainer.devices.wahoo

import android.content.Context
import android.util.Log
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceType
import com.clebi.trainer.devices.DiscoveredListener
import com.clebi.trainer.devices.NetworkChangeListener
import com.clebi.trainer.devices.NetworkState
import com.clebi.trainer.devices.NetworkType
import com.clebi.trainer.devices.TrainerApi
import com.wahoofitness.connector.HardwareConnector
import com.wahoofitness.connector.HardwareConnector.Listener
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.HardwareConnectorTypes
import com.wahoofitness.connector.conn.connections.SensorConnection
import com.wahoofitness.connector.conn.connections.params.ConnectionParams
import com.wahoofitness.connector.listeners.discovery.DiscoveryListener

/**
 * WahooTrainerApi is the implementation of TrainerApi which manages the communication with devices.
 */
class WahooTrainerApi(context: Context) : TrainerApi {

    companion object {
        val TAG = "WahooTrainerApi"

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
    private class ServiceListener : Listener {
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
            val type = networkTypesConverter[networkType] ?: return
            val state = networkStateConverter[networkState] ?: throw Error("unable to get network state")
            lastNetworkState[type] = state
            Log.d(TAG, "state listeners: ${listeners.count()}")
            listeners.forEach {
                it(type, state)
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

    private inner class SearchListener(
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
                it(
                    WahooDevice(
                        params.id,
                        params.antId,
                        type,
                        params.name,
                        params
                    )
                )
            }
        }
    }

    private val discoveredListeners = mutableListOf<DiscoveredListener>()
    private val searchListener =
        SearchListener(this.discoveredListeners)
    private val serviceListener = ServiceListener()
    private val hardwareConnector = HardwareConnector(context, serviceListener)

    override fun destroy() {
        hardwareConnector.shutdown()
    }

    override fun searchForDevices() {
        Log.d(TAG, "start discovery")
        hardwareConnector.startDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    override fun stopSearchForDevices() {
        Log.d(TAG, "stop discovery")
        hardwareConnector.stopDiscovery(searchListener, HardwareConnectorTypes.NetworkType.BTLE)
    }

    override fun connectToDevice(device: Device): WahooConnectedDevice {
        val connectedDevice = WahooConnectedDevice(device)
        hardwareConnector.requestSensorConnection(device.params as ConnectionParams, connectedDevice)
        return connectedDevice
    }

    override fun listenDevicesDiscovery(listener: DiscoveredListener) {
        discoveredListeners.add(listener)
    }

    override fun unlistenDevicesDiscovery(listener: DiscoveredListener) {
        discoveredListeners.remove(listener)
    }

    override fun listenNetworkChange(listener: NetworkChangeListener) {
        serviceListener.addListener(listener)
    }

    override fun unlistenNetworkChange(listener: NetworkChangeListener) {
        serviceListener.removeListener(listener)
    }
}
