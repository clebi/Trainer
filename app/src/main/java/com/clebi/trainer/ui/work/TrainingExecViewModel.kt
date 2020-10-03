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
class TrainingExecViewModel(val training: Training) : ViewModel() {
    private val totalTime = training.steps.stream().mapToInt {
        it.duration
    }.sum()

    private val _currentStep = MutableLiveData(0)

    /** Current step of the training */
    val currentStep: LiveData<Int> = _currentStep

    private val _remainingTotalTime = MutableLiveData(totalTime)

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

    fun setProgress(totalTime: Int, stepTime: Int, currentStep: Int) {
        _remainingTotalTime.postValue(this.totalTime - totalTime)
        _remainingStepTime.postValue(training.steps[currentStep].duration - stepTime)
        _currentStep.postValue(currentStep + 1)
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
