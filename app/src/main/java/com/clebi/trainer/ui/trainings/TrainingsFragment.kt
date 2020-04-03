package com.clebi.trainer.ui.trainings

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import com.clebi.trainer.devices.wahoo.WahooTrainerService
import com.clebi.trainer.trainings.Training
import kotlinx.android.synthetic.main.dialog_training_name.view.*
import kotlinx.android.synthetic.main.fragment_trainings.view.*

/**
 * TrainingsFragment manages the trainings.
 */
class TrainingsFragment : Fragment() {

    companion object {
        private const val TAG = "TrainingsFragment"
        private const val modelInitializedKey = "trainings_model_init"
    }

    private val trainingsModel: TrainingsModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: $savedInstanceState")
        val initialize = savedInstanceState?.getBoolean(modelInitializedKey)
        if (initialize == null || !initialize) {
            trainingsModel.readFromStorage()
        }
        Intent(context, WahooTrainerService::class.java).also {
            activity?.startService(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(modelInitializedKey, true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trainings, container, false)
        val trainingListAdapter = TrainingsListAdapter(listOf(), { position ->
            Log.d(TAG, "click on training: $position")
            val action = TrainingsFragmentDirections.actionNavHomeToTrainingFragment(position)
            findNavController().navigate(action)
        }, { position -> trainingsModel.removeTraining(position) })
        trainingsModel.trainings.observe(viewLifecycleOwner, Observer {
            trainingListAdapter.setTrainings(it)
        })
        val trainingsLayoutManager = LinearLayoutManager(context)
        view.trainings_list.apply {
            adapter = trainingListAdapter
            layoutManager = trainingsLayoutManager
        }
        view.trainings_list.addItemDecoration(
            DividerItemDecoration(
                view.trainings_list.context,
                trainingsLayoutManager.orientation
            )
        )
        view.training_new.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getString(R.string.training_new_dialog_title))
            val dialogView = inflater.inflate(R.layout.dialog_training_name, view as ViewGroup, false)
            builder.setView(dialogView)
            builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                val okBtn = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                Log.d(TAG, "new training name: ${dialogView.training_name.text}")
                dialog.dismiss()
                val position = trainingsModel.addTraining(Training(dialogView.training_name.text.toString(), listOf()))
                Log.d(TAG, "new training at position: $position")
                val action = TrainingsFragmentDirections.actionNavHomeToTrainingFragment(position)
                findNavController().navigate(action)
            }
            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            val dialog = builder.create() as AlertDialog
            dialog.show()
            // The positive button is disabled till training name is correct
            val okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okBtn.isEnabled = false
            dialogView.training_name.addTextChangedListener { text: Editable? ->
                if (text.isNullOrEmpty() || text.isBlank() || text.length < 3) {
                    okBtn.isEnabled = false
                    dialogView.training_name.error = resources.getString(R.string.training_name_error)
                } else {
                    okBtn.isEnabled = true
                    dialogView.training_name.error = null
                }
            }
        }
        return view
    }

    override fun onPause() {
        trainingsModel.saveToStorage()
        super.onPause()
    }
}
