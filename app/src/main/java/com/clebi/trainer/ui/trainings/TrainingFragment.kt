package com.clebi.trainer.ui.trainings

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.trainings.TrainingStep
import kotlinx.android.synthetic.main.dialog_training_step.view.*
import kotlinx.android.synthetic.main.fragment_training.view.*

/**
 * TrainingFragment display a training.
 */
class TrainingFragment : Fragment() {

    companion object {
        private const val TAG = "TrainingFragment"
    }

    private val args: TrainingFragmentArgs by navArgs()
    private val trainingsModel: TrainingsModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val position = args.trainingPosition
        Log.d(TAG, "training position to get: $position")
        val view = inflater.inflate(R.layout.fragment_training, container, false)
        val trainingStepsListAdapter = TrainingStepsListAdapter(listOf())
        view.training_steps.apply {
            adapter = trainingStepsListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        trainingsModel.trainings.observe(viewLifecycleOwner, Observer { trainings ->
            val training = trainings[position]
            Log.d(TAG, "training: $training")
            view.training_title.text = training.name
            trainingStepsListAdapter.setTrainingSteps(training.steps)
        })
        view.training_launch.setOnClickListener {
            val action = TrainingFragmentDirections.actionTrainingFragmentToTrainingExec(position)
            findNavController().navigate(action)
        }
        view.training_step_add.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(context)
            val dialogView = inflater.inflate(R.layout.dialog_training_step, view as ViewGroup, false)
            dialogBuilder.setView(dialogView)
            dialogBuilder.setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.cancel()
            }
            dialogBuilder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                Log.d(TAG, "new step: ${dialogView.training_duration.text} - ${dialogView.training_power.text}")
                dialog.dismiss()
                val training = trainingsModel.trainings.value!![position]
                val duration = Format.durationFromString(dialogView.training_duration.text.toString())
                val power = dialogView.training_power.text.toString().toShort()
                val steps = training.steps.toMutableList()
                steps.add(TrainingStep(duration, power))
                val newTraining = Training(training.name, steps)
                trainingsModel.replaceTraining(position, newTraining)
            }
            dialogBuilder.show()
        }
        return view
    }
}
