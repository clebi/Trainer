package com.clebi.trainer.ui.trainings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.trainings.TrainingStep
import com.clebi.trainer.trainings.TrainingsStorage
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class TrainingsModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun testReadFromStorage() {
        val time = 20000L
        val trainings = listOf(
            Training("test_0", listOf(TrainingStep(120, 150))), Training(
                "test_1", listOf(
                    TrainingStep(24, 120)
                )
            )
        )
        val container = TrainingsStorage.TrainingStorageContainer(time, "test", trainings)
        val storage = Mockito.mock(TrainingsStorage::class.java)
        Mockito.`when`(storage.read()).thenReturn(container)
        Mockito.`when`(storage.isAccessible()).thenReturn(true)
        val model = TrainingsModel(arrayOf(storage))
        model.readFromStorage()
        Truth.assertThat(model.trainings.value).containsExactlyElementsIn(trainings)
    }

    @Test
    fun testSaveToStorage() {
        val trainings = listOf(
            Training("test_0", listOf(TrainingStep(120, 150))), Training(
                "test_1", listOf(
                    TrainingStep(24, 120)
                )
            )
        )
        val storage = Mockito.mock(TrainingsStorage::class.java)
        Mockito.`when`(storage.isAccessible()).thenReturn(true)
        val model = TrainingsModel(arrayOf(storage))
        trainings.forEach {
            model.addTraining(it)
        }
        model.saveToStorage()
        Mockito.verify(storage).write(any(), eq(trainings))
    }

    @Test
    fun testAddTraining() {
        val training = Training("test", listOf(TrainingStep(150, 100)))
        val storage = Mockito.mock(TrainingsStorage::class.java)
        val model = TrainingsModel(arrayOf(storage))
        model.addTraining(training)
        Truth.assertThat(model.trainings.value).hasSize(1)
        Truth.assertThat(model.trainings.value).contains(training)
    }

    @Test
    fun testReplaceTraining() {
        val training = Training("test", listOf(TrainingStep(150, 100)))
        val trainingReplace = Training("test_replace", listOf(TrainingStep(240, 300)))
        val storage = Mockito.mock(TrainingsStorage::class.java)
        val model = TrainingsModel(arrayOf(storage))
        model.addTraining(training)
        Truth.assertThat(model.trainings.value).hasSize(1)
        model.replaceTraining(0, trainingReplace)
        Truth.assertThat(model.trainings.value).hasSize(1)
        Truth.assertThat(model.trainings.value).contains(trainingReplace)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testReplaceTrainingBadPosition() {
        val training = Training("test", listOf(TrainingStep(150, 100)))
        val trainingReplace = Training("test_replace", listOf(TrainingStep(240, 300)))
        val storage = Mockito.mock(TrainingsStorage::class.java)
        val model = TrainingsModel(arrayOf(storage))
        model.addTraining(training)
        Truth.assertThat(model.trainings.value).hasSize(1)
        model.replaceTraining(1, trainingReplace)
    }
}
