package com.clebi.trainer.devices.wahoo

import android.util.Log
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectedDeviceListener
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.DeviceConnectionStatus
import com.wahoofitness.connector.HardwareConnectorEnums
import com.wahoofitness.connector.capabilities.Capability
import com.wahoofitness.connector.capabilities.Kickr
import com.wahoofitness.connector.conn.connections.SensorConnection
import kotlin.properties.Delegates

/**
 * WahooConnectedDevice communicates with a wahoo device. It can send commands and receive notifications.
 */
class WahooConnectedDevice(override val device: Device) : ConnectedDevice, SensorConnection.Listener {
    companion object {
        private const val TAG = "WahooConnectedDevice"
    }

    /** List of all listeners registered */
    private val listeners = mutableListOf<ConnectedDeviceListener>()

    /** Connection to the sensor */
    private var sensorConnection: SensorConnection? = null

    /** status if the device */
    override var status: DeviceConnectionStatus
        by Delegates.observable(DeviceConnectionStatus.NOT_CONNECTED) { _, _, _ ->
            listeners.forEach {
                it(this)
            }
        }

    /** capabilities of the device */
    override var capabilities: List<DeviceCapability> by Delegates.observable(listOf()) { _, _, _ ->
        listeners.forEach {
            it(this)
        }
    }

    /**
     * Add a listener.
     * @param listener listener to add.
     */
    override fun addListener(listener: ConnectedDeviceListener) {
        listeners.add(listener)
        listener(this)
    }

    /**
     * Remove listener.
     * @param listener listener to remove.
     */
    override fun removeListener(listener: ConnectedDeviceListener) {
        listeners.remove(listener)
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
        sensorConnection = if (status == DeviceConnectionStatus.CONNECTED) {
            connection
        } else {
            null
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
        val deviceCapability = when (capability) {
            Capability.CapabilityType.Kickr -> DeviceCapability.BIKE_TRAINER
            Capability.CapabilityType.BikePower -> DeviceCapability.BIKE_POWER
            else -> null
        }
            ?: return
        val newCapabilities = capabilities.toMutableList()
        newCapabilities.add(deviceCapability)
        Log.d(TAG, "onNewCapabilityDetected: new capabilities: $newCapabilities")
        capabilities = newCapabilities
    }

    /**
     * Get a capability.
     * @param capability the capability to get.
     * @return the corresponding capability.
     */
    private fun getCapability(capability: DeviceCapability): Capability {
        if (sensorConnection == null) {
            throw IllegalStateException("device is not connected")
        }
        if (!capabilities.contains(capability)) {
            throw IllegalStateException("capability: $capability does not exists for this device")
        }
        val capa = when (capability) {
            DeviceCapability.BIKE_TRAINER -> Capability.CapabilityType.BikeTrainer
            DeviceCapability.BIKE_POWER -> Capability.CapabilityType.BikePower
        }
        return sensorConnection!!.getCurrentCapability(capa)!!
    }

    /**
     * Get the bike trainer capability.
     * @return the bike trainer.
     */
    override fun getBikeTrainer(): com.clebi.trainer.devices.BikeTrainer {
        val trainer = getCapability(DeviceCapability.BIKE_TRAINER) as Kickr
        return WahooBikeTrainer(trainer)
    }
}
