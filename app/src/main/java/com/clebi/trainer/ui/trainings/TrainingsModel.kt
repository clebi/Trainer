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
        val epoch = System.currentTimeMillis() / 1000;
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
}
