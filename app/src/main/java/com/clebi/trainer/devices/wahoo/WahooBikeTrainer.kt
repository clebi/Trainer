package com.clebi.trainer.devices.wahoo

import com.clebi.trainer.devices.BikeTrainer
import com.wahoofitness.connector.capabilities.Kickr

/**
 * WahooBikeTrainer is an implementation of BikeTrainer using wahoo api.
 */
class WahooBikeTrainer(
    private val trainer: Kickr
) : BikeTrainer {
    /**
     * @see BikeTrainer.setPowerTarget
     */
    override fun setPowerTarget(power: Short) {
        trainer.sendSetErgMode(power.toInt())
    }

    /**
     * @see BikeTrainer.getPowerTarget
     */
    override fun getPowerTarget(): Short {
        return trainer.ergModePower.toShort()
    }
}
