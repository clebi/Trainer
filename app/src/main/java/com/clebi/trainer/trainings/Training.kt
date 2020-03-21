package com.clebi.trainer.trainings

/**
 * TrainingStep contains data of a training step, the duration of the step and the power target.
 */
data class TrainingStep(val duration: Int, val power: Short)

/**
 * Training represents a training. It contains all data necessary to a training.
 */
data class Training(val name: String, val steps: List<TrainingStep>)
