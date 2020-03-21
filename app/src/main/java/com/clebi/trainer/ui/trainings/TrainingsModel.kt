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

    fun addTraining(training: Training) {
        val trainings = (_trainings.value ?: listOf()).toMutableList()
        trainings.add(training)
        _trainings.apply {
            value = trainings
        }
    }
}
