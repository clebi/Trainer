package com.clebi.trainer

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.clebi.trainer.model.Device
import com.clebi.trainer.model.DeviceType
import com.clebi.trainer.ui.config.ConfigModel
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


class ConfigModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testAddDevice() {
        val model = ConfigModel()
        model.addDevice(Device("test", 123456, DeviceType.TRAINER, "test_name"))
        Truth.assertThat(model.devices.value).hasSize(1)
        Truth.assertThat(model.devices.value).contains(Device("test", 123456, DeviceType.TRAINER, "test_name"))
    }
}