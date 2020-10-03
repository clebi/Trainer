package com.clebi.trainer.devices

/**
 * TrainingsStorage reads and saves storage to target.
 */
interface ConnectedDevicesStorage {

    /**
     * Type of the storage implementation.
     */
    val storageType: String

    /**
     * Is the storage accessible ?
     *
     * @return true if it is, false if not.
     */
    fun isAccessible(): Boolean

    /**
     * Read the devices.
     *
     * @return the list of training
     */
    fun read(): List<Device>

    /**
     * Write the trainings.
     *
     * @param devices trainings to write.
     */
    fun write(saveTime: Long, devices: List<ConnectedDevice>)
}
