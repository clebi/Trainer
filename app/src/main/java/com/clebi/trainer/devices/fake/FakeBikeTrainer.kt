package com.clebi.trainer.devices.fake

import android.util.Log
import com.clebi.trainer.devices.BikeTrainer

class FakeBikeTrainer : BikeTrainer {
    companion object {
        private const val TAG = "FakeBikeTrainer"
    }

    private var powerTarget = 0.toShort()

    override fun setPowerTarget(power: Short) {
        Log.i(TAG, "setPowerTarget: $power")
        powerTarget = power
    }

    override fun getPowerTarget(): Short {
        Log.i(TAG, "getPowerTarget: $powerTarget")
        return powerTarget
    }
}
