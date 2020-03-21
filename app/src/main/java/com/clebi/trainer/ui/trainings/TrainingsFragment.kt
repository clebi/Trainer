package com.clebi.trainer.ui.trainings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import com.clebi.trainer.devices.wahoo.WahooTrainerService
import com.clebi.trainer.trainings.Training
import com.clebi.trainer.trainings.TrainingStep
import kotlinx.android.synthetic.main.fragment_trainings.view.*

/**
 * TrainingsFragment manages the trainings.
 */
class TrainingsFragment : Fragment() {

    companion object {
        private const val TAG = "TrainingsFragment"
    }

    private val trainingsModel: TrainingsModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainingsModel.addTraining(
            Training(
                "Test1", listOf(
                    TrainingStep(120, 150),
                    TrainingStep(60, 80)
                )
            )
        )
        trainingsModel.addTraining(Training("Test2", listOf(TrainingStep(240, 130))))
        Intent(context, WahooTrainerService::class.java).also {
            activity?.startService(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trainings, container, false)
        val trainingListAdapter = TrainingsListAdapter(listOf()) { position ->
            Log.d(TAG, "click on training: $position")
            val action = TrainingsFragmentDirections.actionNavHomeToTrainingFragment(position)
            findNavController().navigate(action)
        }
        trainingsModel.trainings.observe(viewLifecycleOwner, Observer {
            trainingListAdapter.setTrainings(it)
        })
        view.trainings_list.apply {
            adapter = trainingListAdapter
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }
}
