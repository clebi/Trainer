package com.clebi.trainer.ui.trainings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.trainings.TrainingsStorage

/**
 * TrainingsModel contains fields for trainings views.
 */
class TrainingsModel(private val trainingsStorages: Array<TrainingsStorage>) : ViewModel() {

    companion object {
        private const val TAG = "TrainingsModel"
    }

    private val _trainings: MutableLiveData<List<Training>> = MutableLiveData(listOf())
    val trainings: LiveData<List<Training>> = _trainings

    /**
     * Replace the list of trainings.
     */
    fun readFromStorage() {
        val trainingsContainer = trainingsStorages.mapNotNull { item ->
            if (item.isAccessible()) {
                item.read()
            } else {
                null
            }
        }.sortedBy {
            it.saveTime
        }
        Log.d(TAG, "trainings container: $trainingsContainer")
        _trainings.apply {
            value = trainingsContainer[0].trainings
        }
    }

    /**
     * Save trainings to storage.
     */
    fun saveToStorage() {
        val epoch = System.currentTimeMillis() / 1000
        val trainings = (_trainings.value ?: listOf())
        trainingsStorages.forEach {
            if (it.isAccessible()) {
                it.write(epoch, trainings)
            }
        }
    }

    /**
     * Append training to the list of trainings.
     * @param training the training to append.
     * @return position of the newly inserted training.
     */
    fun addTraining(training: Training): Int {
        val trainings = (_trainings.value ?: listOf()).toMutableList()
        trainings.add(training)
        val lastIndex = trainings.lastIndex
        _trainings.apply {
            value = trainings
        }
        return lastIndex
    }

    /**
     * Replace a training in the list of training.
     * @param position position of the training to replace.
     * @param training trainings to put at the desired position.
     */
    fun replaceTraining(position: Int, training: Training) {
        val trainings = (_trainings.value ?: listOf()).toMutableList()
        if (position >= trainings.count()) {
            throw IllegalArgumentException("position is outside of the trainings list")
        }
        trainings[position] = training
        _trainings.apply {
            value = trainings
        }
    }

    /**
     * Remove a training from the list of trainings.
     *  @param position position of the training to remove.
     */
    fun removeTraining(position: Int) {
        val trainings = (_trainings.value ?: listOf()).toMutableList()
        if (position >= trainings.count()) {
            throw IllegalArgumentException("position is outside of the trainings list")
        }
        trainings.removeAt(position)
        _trainings.apply {
            value = trainings
        }
    }

    /**
     * Swap to trainings in the list.
     * @param position position of the training.
     * @param from original step position.
     * @param to destination step position.
     */
    fun moveStep(position: Int, from: Int, to: Int) {
        if (position >= _trainings.value!!.count()) {
            throw IllegalArgumentException("position is outside of the trainings list")
        }
        val training = _trainings.value!![position]
        if (from < 0 || from >= training.steps.count()) {
            throw IllegalArgumentException("from is outside of the training step list")
        }
        if (to < 0 || to >= training.steps.count()) {
            throw IllegalArgumentException("to is outside of the training step list")
        }
        val newSteps = training.steps.toMutableList()
        if (from > to) {
            newSteps.add(to, training.steps[from])
            newSteps.removeAt(from + 1)
        } else {
            newSteps.add(to + 1, training.steps[from])
            newSteps.removeAt(from)
        }
        val newTraining = training.copy(steps = newSteps)
        replaceTraining(position, newTraining)
    }

    /**
     * Delete a step from a training.
     * @param position position of the training.
     * @param stepPosition position of the step to remove.
     */
    fun deleteStep(position: Int, stepPosition: Int) {
        if (position >= _trainings.value!!.count()) {
            throw IllegalArgumentException("position is outside of the trainings list")
        }
        val training = _trainings.value!![position]
        if (stepPosition < 0 || stepPosition >= training.steps.count()) {
            throw IllegalArgumentException("from is outside of the training step list")
        }
        val newSteps = training.steps.toMutableList()
        newSteps.removeAt(stepPosition)
        replaceTraining(position, training.copy(steps = newSteps))
    }
}
