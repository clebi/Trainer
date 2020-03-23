package com.clebi.trainer.trainings

/**
 * TrainingsStorage reads and saves storage to target.
 */
interface TrainingsStorage {

    /**
     * Read the trainings.
     * @return the list of training
     */
    fun read(): List<Training>

    /**
     * Write the trainings.
     * @param trainings trainings to write.
     * @param editor the shared preferences editor.
     */
    fun write(trainings: List<Training>)
}
