package com.clebi.trainer.devices

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.clebi.trainer.devices.wahoo.WahooTrainerApi

/**
 * TrainerService is responsible of the discovery and connection to devices.
 */
class TrainerService : Service() {
    companion object {
        lateinit var instance: TrainerService
    }

    private lateinit var trainerApi: TrainerApi

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        trainerApi = WahooTrainerApi(applicationContext)
        // trainerApi = FakeTrainerApi()
        instance = this
    }

    override fun onDestroy() {
        trainerApi.destroy()
        super.onDestroy()
    }

    fun provider(): String {
        return trainerApi.provider
    }

    fun searchForDevices() {
        trainerApi.searchForDevices()
    }

    fun stopSearchForDevices() {
        trainerApi.stopSearchForDevices()
    }

    fun connectToDevice(device: Device): ConnectedDevice {
        return trainerApi.connectToDevice(device)
    }

    fun listenDevicesDiscovery(listener: DiscoveredListener) {
        trainerApi.listenDevicesDiscovery(listener)
    }

    fun unlistenDevicesDiscovery(listener: DiscoveredListener) {
        trainerApi.unlistenDevicesDiscovery(listener)
    }

    fun listenNetworkChange(listener: NetworkChangeListener) {
        trainerApi.listenNetworkChange(listener)
    }

    fun unlistenNetworkChange(listener: NetworkChangeListener) {
        trainerApi.unlistenNetworkChange(listener)
    }
}
