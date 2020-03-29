package com.clebi.trainer.ui.trainings

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.TrainingStep
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
        val trainingStepsListAdapter = TrainingStepsListAdapter(listOf()) { stepPosition ->
            val training = trainingsModel.trainings.value!![position]
            val dialog =
                TrainingStepDialog(context!!, view as ViewGroup, training.steps[stepPosition]) { duration, power ->
                    val steps = training.steps.toMutableList()
                    steps[stepPosition] = TrainingStep(Format.durationFromString(duration), power.toShort())
                    val newTraining = training.copy(steps = steps)
                    trainingsModel.replaceTraining(position, newTraining)
                }
            dialog.create()
            dialog.show()
        }
        val stepsLayoutManager = LinearLayoutManager(context)
        view.training_steps.apply {
            adapter = trainingStepsListAdapter
            layoutManager = stepsLayoutManager
        }
        val touchHelper = ItemTouchHelper(trainingStepsListAdapter.TouchHelper({ from: Int, to: Int ->
            if (from == to) {
                return@TouchHelper
            }
            Log.d(TAG, "dragFrom: $from - dragTo: $to")
            try {
                trainingsModel.moveStep(position, from, to)
            } catch (exc: IllegalArgumentException) {
                Log.w(TAG, exc)
            }
        }, { stepPosition ->
            trainingsModel.deleteStep(position, stepPosition)
        }))
        touchHelper.attachToRecyclerView(view.training_steps)
        view.training_steps.addItemDecoration(
            DividerItemDecoration(
                view.training_steps.context,
                stepsLayoutManager.orientation
            )
        )
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
            val training = trainingsModel.trainings.value!![position]
            val dialog =
                TrainingStepDialog(context!!, view as ViewGroup, null) { duration, power ->
                    val steps = training.steps.toMutableList()
                    steps.add(TrainingStep(Format.durationFromString(duration), power.toShort()))
                    val newTraining = training.copy(steps = steps)
                    trainingsModel.replaceTraining(position, newTraining)
                }
            dialog.create()
            dialog.show()
        }
        return view
    }
}
