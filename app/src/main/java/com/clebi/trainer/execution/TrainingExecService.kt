package com.clebi.trainer.execution

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.clebi.trainer.R
import com.clebi.trainer.StaticConfig
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.ui.work.TrainingExecActivity
import java.util.Timer
import java.util.TimerTask
import kotlin.properties.Delegates

class TrainingExecService : Service() {
    companion object {
        const val NOTIFICATION_ID = 1
        private const val TAG = "TrainingExecService"
    }

    inner class LocalBinder : Binder() {
        fun getService(): TrainingExecService = this@TrainingExecService
    }

    interface Listener {
        fun started()
        fun progressed(stepTime: Int, totalTime: Int, stepIndex: Int)
        fun paused()
        fun stopped()
        fun ended()
    }

    private val binder = LocalBinder()

    private var trainer: BikeTrainer? = null
    private var currentTraining: Training? = null
    private var timer: Timer? = null
    private val listeners = mutableListOf<Listener>()

    private var _initiated = false
    private var time = 0
    private var currentStep = 0
    private var processStepTime = 0
    private var currentPower: Short by Delegates.observable(0.toShort()) { _, _, newValue ->
        Log.d(TAG, "setPowerTarget: $newValue")
        trainer?.setPowerTarget(newValue)
    }

    fun listen(listener: Listener) {
        listener.progressed(time - processStepTime, time, currentStep)
        listeners.add(listener)
    }

    fun unlisten(listener: Listener) {
        listeners.remove(listener)
    }

    fun initialize(trainer: BikeTrainer, training: Training) {
        this.trainer = trainer
        currentTraining = training
        time = 0
        currentStep = 0
        processStepTime = 0
        _initiated = true
    }

    fun start() {
        if (trainer == null || currentTraining == null) {
            throw IllegalStateException("service not initialzed")
        }
        if (timer != null) {
            throw IllegalStateException("already executing a training")
        }
        timer = Timer()
        timer!!.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    time += 1
                    Log.d(TAG, "time: $time - currentStep: $currentStep")
                    var stepTime = time - processStepTime
                    Log.d(TAG, "stepTime: $stepTime")
                    if (stepTime >= currentTraining!!.steps[currentStep].duration) {
                        Log.d(TAG, "next step from: $currentStep")
                        processStepTime += currentTraining!!.steps[currentStep].duration
                        Log.d(TAG, "next step processed step time: $processStepTime")
                        currentStep += 1
                        if (currentStep >= currentTraining!!.steps.count()) {
                            Log.d(TAG, "no more step to run")
                            timer!!.cancel()
                            listeners.forEach {
                                it.progressed(stepTime, time, currentStep - 1)
                                it.ended()
                            }
                            return
                        }
                        currentPower = currentTraining!!.steps[currentStep].power
                        stepTime = 0
                    }
                    listeners.forEach {
                        it.progressed(stepTime, time, currentStep)
                    }
                }
            },
            0,
            1000
        )
        currentPower = currentTraining!!.steps[0].power
        listeners.forEach {
            it.started()
        }
    }

    fun stop() {
        trainer = null
        currentTraining = null
        timer?.cancel()
        timer = null
        listeners.forEach {
            it.stopped()
        }
        stopService()
        Log.d(TAG, "service stopped")
    }

    fun pause() {
        timer?.cancel()
        timer = null
        listeners.forEach {
            it.paused()
        }
    }

    fun initiated() = _initiated

    fun stopService() {
        stopSelf()
        stopForeground(true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent = Intent(this, TrainingExecActivity::class.java).let {
            if (intent !== null && intent.extras !== null) {
                it.putExtras(intent.extras!!)
            }
            it.flags = FLAG_ACTIVITY_NEW_TASK
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val notification = Notification.Builder(this, StaticConfig.TRAINING_SERVICE_CHANNEL_ID)
            .setContentTitle(getText(R.string.service_notif_title))
            .setContentText(getText(R.string.service_notif_title))
            .setSmallIcon(R.drawable.notif_icon)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}
