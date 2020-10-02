package com.clebi.trainer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.clebi.trainer.devices.ConnectedDevicesStorage
import com.clebi.trainer.devices.SharedPrefsConnectedDevicesStorage
import com.clebi.trainer.devices.TrainerService
import com.clebi.trainer.trainings.FileTrainingStorage
import com.clebi.trainer.trainings.SharedPrefsTrainingStorage
import com.clebi.trainer.trainings.TrainingsStorage
import com.clebi.trainer.ui.config.DevicesConfigModel
import com.clebi.trainer.ui.trainings.TrainingsModel
import com.google.android.material.navigation.NavigationView

/**
 * TrainingsModelFactory builds trainings model with constructor params.
 */
private class TrainingsModelFactory(private val trainingsStorages: Array<TrainingsStorage>) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingsModel(trainingsStorages) as T
    }
}

/**
 * DevicesConfigModelFactory builds trainings model with constructor params.
 */
class DevicesModelFactory(private val devicesStorages: Array<ConnectedDevicesStorage>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DevicesConfigModel(devicesStorages) as T
    }
}

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the trainings model
        val trainingsModel = ViewModelProvider(
            this,
            TrainingsModelFactory(
                arrayOf(
                    FileTrainingStorage(applicationContext),
                    SharedPrefsTrainingStorage(applicationContext)
                )
            )
        ).get(
            TrainingsModel::class.java
        )

        trainingsModel.readFromStorage()

        // Initialize the devices config model
        ViewModelProvider(
            this,
            DevicesModelFactory(
                arrayOf(
                    SharedPrefsConnectedDevicesStorage(applicationContext)
                )
            )
        ).get(
            DevicesConfigModel::class.java
        )

        Intent(this, TrainerService::class.java).also {
            this.startService(it)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_trainings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
