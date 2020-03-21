package com.clebi.trainer.ui.trainings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import kotlinx.android.synthetic.main.fragment_training.view.*;

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
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training, container, false)
        Log.d(TAG, "training position to get: ${args.trainingPositition}")
        val training = trainingsModel.trainings.value!![args.trainingPositition]
        Log.d(TAG, "training: $training")
        view.training_title.text = training.name
        view.training_steps.apply {
            adapter = TrainingStepsListAdapter(training.steps)
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }
}
