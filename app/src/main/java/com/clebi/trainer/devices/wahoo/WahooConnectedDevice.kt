package com.clebi.trainer.devices.wahoo

import android.util.Log
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectionStatusCallback
import com.clebi.trainer.devices.DeviceConnectionStatus
import com.clebi.trainer.model.Device
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.conn.connections.SensorConnection
import kotlin.properties.Delegates

/**
 * WahooConnectedDevice communicates with a wahoo device. It can send commands and receive notifications.
 */
class WahooConnectedDevice(override val device: Device) : ConnectedDevice, SensorConnection.Listener {
    companion object {
        private const val TAG = "WahooConnectedDevice"
    }

    private val connectionStatusListeners = mutableListOf<ConnectionStatusCallback>()

    /** status if the device */
    override var status: DeviceConnectionStatus
        by Delegates.observable(DeviceConnectionStatus.NOT_CONNECTED) { _, _, new ->
            connectionStatusListeners.forEach {
                it(new)
            }
        }

    /**
     * Add a listener for status.
     * @param listener listener to add.
     */
    override fun addConnectionStatusListeners(listener: ConnectionStatusCallback) {
        connectionStatusListeners.add(listener)
        listener(status)
    }

    /**
     * Remove status listener.
     * @param listener listener to remove.
     */
    override fun removeConnectionStatusListeners(listener: ConnectionStatusCallback) {
        connectionStatusListeners.remove(listener)
    }

    /**
     * Notify sensor state changes.
     * @param connection the connection to the device.
     * @param state state of connection.
     */
    override fun onSensorConnectionStateChanged(
        connection: SensorConnection,
        state: HardwareConnectorEnums.SensorConnectionState
    ) {
        Log.d(TAG, "onSensorConnectionStateChanged: ${connection.id} - $state")
        status = when (state) {
            HardwareConnectorEnums.SensorConnectionState.DISCONNECTED -> DeviceConnectionStatus.NOT_CONNECTED
            HardwareConnectorEnums.SensorConnectionState.CONNECTING -> DeviceConnectionStatus.CONNECTING
            HardwareConnectorEnums.SensorConnectionState.CONNECTED -> DeviceConnectionStatus.CONNECTED
            HardwareConnectorEnums.SensorConnectionState.DISCONNECTING -> DeviceConnectionStatus.DISCONNECTING
        }
    }

    /**
     * Notify of sensor connection errors.
     * @param connection the connection to the device.
     * @param error the error description.
     */
    override fun onSensorConnectionError(
        connection: SensorConnection,
        error: HardwareConnectorEnums.SensorConnectionError
    ) {
        Log.d(TAG, "onSensorConnectionError: $connection - $error")
    }

    /**
     * Notify of a new capability for the device.
     * @param connection the connection to the device.
     */
    override fun onNewCapabilityDetected(connection: SensorConnection, capability: Capability.CapabilityType) {
        Log.d(TAG, "onNewCapabilityDetected: $connection - $capability")
    }
}
