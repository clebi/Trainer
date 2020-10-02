package com.clebi.trainer.ui.work

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebi.trainer.R
import com.clebi.trainer.TrainerApp
import com.clebi.trainer.devices.BikeTrainer
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.execution.TrainingExecService
import com.clebi.trainer.trainings.FileTrainingStorage
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.SharedPrefsTrainingStorage
import com.clebi.trainer.trainings.TrainingsStorage
import com.clebi.trainer.ui.trainings.TrainingsModel
import kotlinx.android.synthetic.main.fragment_training_exec.*

/**
 * TrainingsModelFactory builds trainings model with constructor params.
 */
class TrainingsModelFactory(private val trainingsStorages: Array<TrainingsStorage>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TrainingsModel(trainingsStorages) as T
    }
}

class TrainingExecActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "TrainingExecActivity"
    }

    private lateinit var serviceIntent: Intent
    private lateinit var viewModel: TrainingExecViewModel
    private lateinit var bikeTrainer: BikeTrainer
    private lateinit var trainingsModel: TrainingsModel
    private var controller: TrainingExecController? = null
    private var trainingExecService: TrainingExecService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            trainingExecService = (service as TrainingExecService.LocalBinder).getService()
            controller = TrainingExecController(
                viewModel,
                (applicationContext as TrainerApp).devices!!,
                trainingExecService!!
            ).also {
                it.init(bikeTrainer)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            trainingExecService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the trainings model
        trainingsModel = ViewModelProvider(
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

        val position: Int = intent.extras?.getInt("training_position")
            ?: throw IllegalArgumentException("missing training position")

        serviceIntent = Intent(this, TrainingExecService::class.java).also { intent ->
            intent.putExtra("training_position", position)
            this.startService(intent)
        }

        setContentView(R.layout.fragment_training_exec)
        val training = trainingsModel.trainings.value!![position]
        viewModel = TrainingExecViewModel(training)
        val devices = (applicationContext as TrainerApp).devices!!
        try {
            val trainerDevice = devices.stream()
                .filter { it.capabilities.contains(DeviceCapability.BIKE_TRAINER) }
                .findFirst()
            if (!trainerDevice.isPresent) {
                throw IllegalStateException("unable to find trainer")
            }
            bikeTrainer = trainerDevice.get().getBikeTrainer()
            training_title.text = training.name
            viewModel.currentStep.observe(
                this,
                {
                    training_step.text = resources.getText(R.string.training_step_pos).toString()
                        .format(it, training.steps.count())
                }
            )
            viewModel.currentPower.observe(
                this,
                {
                    training_power.text = "${it}W"
                    controller?.setTrainerPower(it)
                }
            )
            viewModel.remainingTotalTime.observe(
                this,
                {
                    training_duration.text = Format.formatShortDuration(it)
                }
            )
            viewModel.remainingStepTime.observe(
                this,
                {
                    training_step_duration.text = Format.formatShortDuration(it)
                }
            )
            viewModel.currentStatus.observe(
                this,
                {
                    val text = when (it) {
                        TrainingStatus.RUN -> resources.getString(R.string.training_status_play)
                        TrainingStatus.PAUSE -> resources.getString(R.string.training_status_paused)
                        TrainingStatus.END -> resources.getString(R.string.training_status_end)
                        TrainingStatus.STOP -> {
                            finish()
                            ""
                        }
                        else -> ""
                    }
                    training_status.text = text
                }
            )
            training_play.setOnClickListener {
                Log.d(TAG, "start: $controller")
                controller?.start()
            }
            training_pause.setOnClickListener {
                controller?.pause()
            }
            training_stop.setOnClickListener {
                controller?.stop()
            }
            training_power_reduce.setOnClickListener {
                controller?.reducePower()
            }
            training_power_increase.setOnClickListener {
                controller?.increasePower()
            }
        } catch (exc: IllegalStateException) {
            val toast = Toast.makeText(
                this,
                resources.getString(R.string.training_device_not_connected),
                Toast.LENGTH_LONG
            )
            toast.setGravity(Gravity.TOP, 0, 25)
            toast.show()
            this.stopService(serviceIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        this.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        this.unbindService(connection)
        super.onStop()
    }
}
