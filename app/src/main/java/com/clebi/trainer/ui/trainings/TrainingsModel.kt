package com.clebi.trainer.ui.trainings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.clebi.trainer.trainings.Training

/**
 * TrainingsModel contains fields for trainings views.
 */
class TrainingsModel : ViewModel() {

    private val _trainings: MutableLiveData<List<Training>> = MutableLiveData(listOf())
    val trainings: LiveData<List<Training>> = _trainings

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
        trainings[position] = training
        _trainings.apply {
            value = trainings
        }
    }
}
