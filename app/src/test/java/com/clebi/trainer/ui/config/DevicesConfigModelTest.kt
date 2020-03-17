package com.clebi.trainer.ui.config

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectionStatusCallback
import com.clebi.trainer.devices.DeviceConnectionStatus
import com.clebi.trainer.model.Device
import com.clebi.trainer.model.DeviceType
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test

data class TestConnectedDevice(override val device: Device, override var status: DeviceConnectionStatus) :
    ConnectedDevice {
    override fun addConnectionStatusListeners(listener: ConnectionStatusCallback) {
        TODO("Not yet implemented")
    }

    override fun removeConnectionStatusListeners(listener: ConnectionStatusCallback) {
        TODO("Not yet implemented")
    }
}

class DevicesConfigModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testAddDevice() {
        val testParams = Object()
        val model = DevicesConfigModel()
        model.addSearchDevice(Device("test", 123456, DeviceType.TRAINER, "test_name", testParams))
        Truth.assertThat(model.searchDevices.value).hasSize(1)
        Truth.assertThat(model.searchDevices.value)
            .contains(Device("test", 123456, DeviceType.TRAINER, "test_name", testParams))
    }

    @Test
    fun testAddConnectedDevice() {
        val testParams = Object()
        val testDevice = Device("test", 123456, DeviceType.TRAINER, "test_name", testParams)
        val model = DevicesConfigModel()
        model.addConnectedDevices(
            TestConnectedDevice(
                testDevice,
                DeviceConnectionStatus.CONNECTED
            )
        )
        Truth.assertThat(model.connectedDevices.value).hasSize(1)
        Truth.assertThat(model.connectedDevices.value)
            .contains(
                TestConnectedDevice(
                    testDevice,
                    DeviceConnectionStatus.CONNECTED
                )
            )
    }
}
