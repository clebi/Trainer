package com.clebi.trainer.ui.work

import java.util.Timer
import java.util.TimerTask

/**
 * TrainingExecController is responsible for actions for all training execution actions.
 */
class TrainingExecController(private val trainingExecViewModel: TrainingExecViewModel) {

    private var timer: Timer? = null

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
