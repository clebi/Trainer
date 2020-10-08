package com.clebi.trainer.execution

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.clebi.trainer.R
import com.clebi.trainer.StaticConfig
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.ui.work.TrainingExecActivity
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.CopyOnWriteArrayList
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

    private val notifier = InnerListener()
    private var extras: Bundle? = null

    private var trainer: BikeTrainer? = null
    private var currentTraining: Training? = null
    private var timer: Timer? = null
    private val listeners = CopyOnWriteArrayList<Listener>()

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
        reset()
        _initiated = true
        listen(notifier)
    }

    private fun reset() {
        time = 0
        currentStep = 0
        processStepTime = 0
    }

    fun start() {
        if (trainer == null || currentTraining == null) {
            throw IllegalStateException("service not initialized")
        }
        if (timer != null) {
            throw IllegalStateException("already executing a training")
        }
        reset()
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
        unlisten(notifier)
        stopSelf()
        stopForeground(true)
    }

    inner class InnerListener : Listener {
        override fun started() {
        }

        override fun progressed(stepTime: Int, totalTime: Int, stepIndex: Int) {
            val step = currentTraining!!.steps[stepIndex]
            val notification = buildNotification(
                extras,
                getText(R.string.service_notif_progress).toString().format(
                    stepIndex + 1,
                    currentTraining?.steps?.size,
                    Format.formatShortDuration(step.duration - stepTime)
                )
            )
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        }

        override fun paused() {
            val notification = buildNotification(extras, getText(R.string.service_notif_pause))
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        }

        override fun stopped() {
        }

        override fun ended() {
            val notification = buildNotification(extras, getText(R.string.service_notif_end))
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
            timer?.cancel()
            timer = null
            currentStep = 0
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        extras = intent?.extras
        startForeground(NOTIFICATION_ID, buildNotification(extras, getText(R.string.service_notif_title)))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun buildNotification(extras: Bundle?, text: CharSequence): Notification {
        val pendingIntent = Intent(this, TrainingExecActivity::class.java).let {
            extras?.run {
                it.putExtras(this)
            }
            it.flags = FLAG_ACTIVITY_NEW_TASK
            PendingIntent.getActivity(this, 0, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        return Notification.Builder(this, StaticConfig.TRAINING_SERVICE_CHANNEL_ID)
            .setContentTitle(getText(R.string.service_notif_title))
            .setContentText(text)
            .setSmallIcon(R.drawable.notif_icon)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()
    }
}
