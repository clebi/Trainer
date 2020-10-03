package com.clebi.trainer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.clebi.trainer.devices.ConnectedDevice

class TrainerApp : Application() {
    var devices: List<ConnectedDevice>? = null

    override fun onCreate() {
        super.onCreate()
        val trainerServiceChannel = NotificationChannel(
            StaticConfig.TRAINING_SERVICE_CHANNEL_ID,
            "Training execution channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        (getSystemService(NotificationManager::class.java) as NotificationManager)
            .createNotificationChannel(trainerServiceChannel)
    }
}
