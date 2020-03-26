package com.clebi.trainer.devices

/**
 * BikeTrainer interacts with a bike trainer device.
 */
interface BikeTrainer {
    /**
     * Set the power target on the trainer.
     * @param power power target.
     */
    fun setPowerTarget(power: Short)

    /**
     * Get the current power target.
     * @return the current power target.
     */
    fun getPowerTarget(): Short
}
