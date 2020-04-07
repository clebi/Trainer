package com.clebi.trainer.ui.work

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.trainings.Training

enum class TrainingStatus {
    NOT_RUN, STOP, PAUSE, RUN, END;
}

/**
 * TrainingExecViewModel contains all necessary information for a training execution.
 */
class TrainingExecViewModel(private val training: Training) : ViewModel() {
    private val _currentStep = MutableLiveData(0)

    /** Current step of the training */
    val currentStep: LiveData<Int> = _currentStep

    private val _remainingTotalTime = MutableLiveData(training.steps.stream().mapToInt {
        it.duration
    }.sum())

    /** remaining time of the training */
    val remainingTotalTime: LiveData<Int> = _remainingTotalTime

    private val _remainingStepTime = MutableLiveData(training.steps[0].duration)

    /** remaining time for the current step of the training */
    val remainingStepTime: LiveData<Int> = _remainingStepTime

    private val _currentPower = MutableLiveData(training.steps[0].power)

    /** current power of the training */
    val currentPower: LiveData<Short> = _currentPower

    private val _currentStatus = MutableLiveData(TrainingStatus.NOT_RUN)

    /** Status of the training */
    val currentStatus: LiveData<TrainingStatus> = _currentStatus

    /**
     * Decrease the total time of the training execution.
     */
    fun decreaseRemainingTotalTime(): Int {
        val time = _remainingTotalTime.value!! - 1
        _remainingTotalTime.postValue(_remainingTotalTime.value!! - 1)
        return time
    }

    /**
     * Decrease the step time of the training execution.
     */
    fun decreaseRemainingStepTime(): Int {
        val time = _remainingStepTime.value!! - 1
        _remainingStepTime.postValue(time)
        return time
    }

    /**
     * Push training to next step.
     */
    fun nextStep() {
        val currentStep = _currentStep.value!! + 1
        if (currentStep >= training.steps.count()) {
            throw IllegalStateException("no more steps")
        }
        val remainingStepTime = training.steps[currentStep].duration
        val currentPower = training.steps[currentStep].power
        _currentStep.postValue(currentStep)
        _remainingStepTime.postValue(remainingStepTime)
        _currentPower.postValue(currentPower)
    }

    /**
     * Training starts.
     */
    fun trainingStart() {
        _currentStatus.postValue(TrainingStatus.RUN)
    }

    /**
     * Training pauses.
     */
    fun trainingPaused() {
        _currentStatus.postValue(TrainingStatus.PAUSE)
    }

    /**
     * Training stops.
     */
    fun trainingStop() {
        _currentStatus.postValue(TrainingStatus.STOP)
    }

    /**
     * Training ends.
     */
    fun trainingEnd() {
        _currentStatus.postValue(TrainingStatus.END)
    }

    /**
     * Change power.
     * @param power new value for power.
     */
    fun changePower(power: Short) {
        _currentPower.postValue(power)
    }
}
