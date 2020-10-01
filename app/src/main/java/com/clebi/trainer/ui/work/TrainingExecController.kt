package com.clebi.trainer.ui.work

import android.util.Log
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.execution.TrainingExecService

/**
 * TrainingExecController is responsible for actions for all training execution actions.
 */
class TrainingExecController(
    private val trainingExecViewModel: TrainingExecViewModel,
    private val devices: List<ConnectedDevice>,
    private val trainingExecService: TrainingExecService
) {
    companion object {
        private const val TAG = "TrainingExecController"
    }

    private lateinit var trainer: BikeTrainer

    private val serviceListener = object : TrainingExecService.Listener {
        override fun started() {
            trainingExecViewModel.trainingStart()
        }

        override fun progressed(stepTime: Int, totalTime: Int, stepIndex: Int) {
            trainingExecViewModel.setProgress(totalTime, stepTime, stepIndex)
        }

        override fun paused() {
            trainingExecViewModel.trainingPaused()
        }

        override fun stopped() {
            trainingExecViewModel.trainingStop()
        }

        override fun ended() {
            Log.d(TAG, "training ended")
            trainingExecViewModel.trainingEnd()
            trainingExecService.stopService()
        }
    }

    /**
     * Initialize the training by getting the trainer device.
     */
    fun init(trainer: BikeTrainer) {
        trainingExecService.listen(serviceListener)
        Log.d(TAG, "initiated: ${trainingExecService.initiated()}")
        if (!trainingExecService.initiated()) {
            trainingExecService.initialize(trainer, trainingExecViewModel.training)
        }
    }

    /**
     * Set the training power target.
     */
    fun setTrainerPower(power: Short) {
        Log.d(TAG, "set power target: $power")
        trainer.setPowerTarget(power)
    }

    /**
     * Start the training.
     */
    fun start() {
        trainingExecService.start()
    }

    /**
     * Pause the training.
     */
    fun pause() {
        trainingExecService.pause()
    }

    /**
     * Stop a training.
     */
    fun stop() {
        trainingExecService.stop()
        trainingExecService.unlisten(serviceListener)
    }

    /**
     * Increase the current power.
     * @param step by how much the power must be increased.
     */
    fun increasePower(step: Short = 5) {
        trainingExecViewModel.changePower((trainingExecViewModel.currentPower.value!! + step).toShort())
    }

    /**
     * Reduce the current power.
     * @param step by how much the power must be reduced.
     */
    fun reducePower(step: Short = 5) {
        trainingExecViewModel.changePower((trainingExecViewModel.currentPower.value!! - step).toShort())
    }
}
