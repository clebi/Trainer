package com.clebi.trainer.ui.trainings

import android.app.AlertDialog
import android.content.Context
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
import com.clebi.trainer.trainings.TrainingStorage
import kotlinx.android.synthetic.main.dialog_training_name.view.*
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
        val prefs = context!!.getSharedPreferences("trainings", Context.MODE_PRIVATE)
        val trainings = TrainingStorage.read(prefs)
        trainingsModel.setTrainings(trainings)
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
        view.training_new.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.training_new_dialog_title))
            val view = inflater.inflate(R.layout.dialog_training_name, view as ViewGroup, false)
            builder.setView(view)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                Log.d(TAG, "new training name: ${view.training_name.text}")
                dialog.dismiss()
                val position = trainingsModel.addTraining(Training(view.training_name.text.toString(), listOf()))
                Log.d(TAG, "new training at position: $position")
                val action = TrainingsFragmentDirections.actionNavHomeToTrainingFragment(position)
                findNavController().navigate(action)
            }
            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        }
        return view
    }

    override fun onPause() {
        val prefs = context!!.getSharedPreferences("trainings", Context.MODE_PRIVATE)
        TrainingStorage.write(trainingsModel.trainings.value!!, prefs)
        super.onPause()
    }
}
