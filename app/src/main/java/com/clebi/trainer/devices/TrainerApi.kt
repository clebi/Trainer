package com.clebi.trainer.devices

/**
 * DiscoveredListener provides interface to notify device discovery to consumer.
 */
typealias DiscoveredListener = (device: Device) -> Unit
typealias NetworkChangeListener = (networkType: NetworkType, networkState: NetworkState) -> Unit

interface TrainerApi {
    val provider: String

    /**
     * Look for devices.
     */
    fun searchForDevices()

    /**
     * Stop looking for devices.
     */
    fun stopSearchForDevices()

    /**
     * Connect to a device.
     * @param device the device to connect to.
     */
    fun connectToDevice(device: Device): ConnectedDevice

    /**
     * Add a listener for discovery of devices.
     */
    fun listenDevicesDiscovery(listener: DiscoveredListener)

    /**
     * Remove a discovery listener.
     */
    fun unlistenDevicesDiscovery(listener: DiscoveredListener)

    /**
     * Listen to network changes.
     */
    fun listenNetworkChange(listener: NetworkChangeListener)

    /**
     * Stop listening to network changes.
     */
    fun unlistenNetworkChange(listener: NetworkChangeListener)

    /**
     * Destroy the api.
     */
    fun destroy()
}
