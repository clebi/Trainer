package com.clebi.trainer.devices.wahoo

import android.util.Log
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.model.Device
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.conn.connections.SensorConnection

/**
 * WahooConnectedDevice communicates with a wahoo device. It can send commands and receive notifications.
 */
class WahooConnectedDevice(override val device: Device) : ConnectedDevice, SensorConnection.Listener {
    companion object {
        private val TAG = "WahooConnectedDevice"
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
        Log.d(TAG, "onSensorConnectionStateChanged: $connection - $state")
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