package com.clebi.trainer.ui.work

import android.util.Log
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.wahoo.WahooTrainerService
import com.clebi.trainer.ui.config.DevicesConfigModel
import java.util.Timer
import java.util.TimerTask

/**
 * TrainingExecController is responsible for actions for all training execution actions.
 */
class TrainingExecController(
    private val trainingExecViewModel: TrainingExecViewModel,
    private val devicesConfigModel: DevicesConfigModel,
    private val trainerService: WahooTrainerService
) {
    companion object {
        private const val TAG = "TrainingExecController"
    }

    private var timer: Timer? = null
    private lateinit var trainer: BikeTrainer

    /**
     * Initialize the training by getting the trainer device.
     */
    fun init() {
        val trainerDevice = devicesConfigModel.connectedDevices.value!!.stream()
            .filter { it.capabilities.contains(DeviceCapability.BIKE_TRAINER) }
            .findFirst()
        if (!trainerDevice.isPresent) {
            throw IllegalStateException("unable to find trainer")
        }
        trainer = trainerDevice.get().getBikeTrainer()
    }

    /**
     * Set the training power target.
     */
    fun setPower(power: Short) {
        Log.d(TAG, "set power target: $power")
        trainer.setPowerTarget(power)
    }

    /**
     * Start the training.
     */
    fun start() {
        timer = Timer()
        timer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                val stepTime = trainingExecViewModel.decreaseRemainingStepTime()
                val totalTime = trainingExecViewModel.decreaseRemainingTotalTime()
                if (totalTime <= 0) {
                    end()
                    return
                }
                if (stepTime <= 0) {
                    trainingExecViewModel.nextStep()
                }
            }
        }, 0, 1000)
        trainingExecViewModel.trainingStart()
    }

    /**
     * Pause the training.
     */
    fun pause() {
        timer?.cancel()
        trainingExecViewModel.trainingPaused()
    }

    /**
     * End a training.
     */
    private fun end() {
        timer?.cancel()
        trainingExecViewModel.trainingEnd()
    }

    /**
     * Stop a training.
     */
    fun stop() {
        timer?.cancel()
        trainingExecViewModel.trainingStop()
    }
}
