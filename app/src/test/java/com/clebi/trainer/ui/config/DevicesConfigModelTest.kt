package com.clebi.trainer.ui.config

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectedDeviceListener
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.DeviceConnectionStatus
import com.clebi.trainer.devices.DeviceType
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test

data class TestConnectedDevice(
    override val device: Device,
    override var status: DeviceConnectionStatus,
    override var capabilities: List<DeviceCapability>
) :
    ConnectedDevice {
    override fun addListener(listener: ConnectedDeviceListener) {
        TODO("Not yet implemented")
    }

    override fun removeListener(listener: ConnectedDeviceListener) {
        TODO("Not yet implemented")
    }

    override fun getBikeTrainer(): BikeTrainer {
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
        model.addSearchDevice(
            Device(
                "test",
                123456,
                DeviceType.TRAINER,
                "test_name",
                testParams
            )
        )
        Truth.assertThat(model.searchDevices.value).hasSize(1)
        Truth.assertThat(model.searchDevices.value)
            .contains(
                Device(
                    "test",
                    123456,
                    DeviceType.TRAINER,
                    "test_name",
                    testParams
                )
            )
    }

    @Test
    fun testAddConnectedDevice() {
        val testParams = Object()
        val testDevice = Device(
            "test",
            123456,
            DeviceType.TRAINER,
            "test_name",
            testParams
        )
        val model = DevicesConfigModel()
        model.addConnectedDevices(
            TestConnectedDevice(
                testDevice,
                DeviceConnectionStatus.CONNECTED,
                listOf()
            )
        )
        Truth.assertThat(model.connectedDevices.value).hasSize(1)
        Truth.assertThat(model.connectedDevices.value)
            .contains(
                TestConnectedDevice(
                    testDevice,
                    DeviceConnectionStatus.CONNECTED,
                    listOf()
                )
            )
    }
}
