package com.clebi.trainer.ui.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.ui.trainings.TrainingFragmentArgs
import com.clebi.trainer.ui.trainings.TrainingsModel
import kotlinx.android.synthetic.main.fragment_training_exec.*
import kotlinx.android.synthetic.main.fragment_training_exec.view.*

/**
 * TrainingExecFragment manages the execution of a training.
 */
class TrainingExecFragment : Fragment() {
    private val args: TrainingFragmentArgs by navArgs()
    private val trainingsModel: TrainingsModel by activityViewModels()

    private lateinit var controller: TrainingExecController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val position = args.trainingPosition
        val view = inflater.inflate(R.layout.fragment_training_exec, container, false)
        val training = trainingsModel.trainings.value!![position]
        val viewModel = TrainingExecViewModel(training)
        controller = TrainingExecController(viewModel)
        view.training_title.text = training.name
        viewModel.currentStep.observe(viewLifecycleOwner, Observer {
            view.training_step.text = resources.getText(R.string.training_step_pos).toString()
                .format(it + 1, training.steps.count())
        })
        viewModel.currentPower.observe(viewLifecycleOwner, Observer {
            view.training_power.text = "${it}W"
        })
        viewModel.remainingTotalTime.observe(viewLifecycleOwner, Observer {
            training_duration.text = Format.formatDuration(it)
        })
        viewModel.remainingStepTime.observe(viewLifecycleOwner, Observer {
            training_step_duration.text = Format.formatDuration(it)
        })
        viewModel.currentStatus.observe(viewLifecycleOwner, Observer {
            val text = when (it) {
                TrainingStatus.RUN -> resources.getString(R.string.training_status_play)
                TrainingStatus.PAUSE -> resources.getString(R.string.training_status_paused)
                TrainingStatus.END -> resources.getString(R.string.training_status_end)
                else -> ""
            }
            training_status.text = text
        })
        view.training_play.setOnClickListener {
            controller.start()
        }
        view.training_pause.setOnClickListener {
            controller.pause()
        }
        view.training_stop.setOnClickListener {
            controller.stop()
            findNavController().popBackStack()
        }
        return view
    }

    override fun onPause() {
        controller.stop()
        super.onPause()
    }
}
