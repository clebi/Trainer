package com.clebi.trainer.trainings

/**
 * TrainingsStorage reads and saves storage to target.
 */
interface TrainingsStorage {

    /**
     * TrainingStorageContainer contains the list of trainings and metadata.
     */
    data class TrainingStorageContainer(val saveTime: Long, val storageType: String, val trainings: List<Training>) {
        val version: Int = 1
    }

    /** Type of the storage implementation. */
    val storageType: String

    /**
     * Is the storage accessible ?
     * @return true if it is, false if not.
     */
    fun isAccessible(): Boolean

    /**
     * Read the trainings.
     * @return the list of training
     */
    fun read(): TrainingStorageContainer

    /**
     * Write the trainings.
     * @param trainings trainings to write.
     */
    fun write(saveTime: Long, trainings: List<Training>)
}
