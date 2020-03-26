package com.clebi.trainer.ui.trainings

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.TrainingStep
import kotlinx.android.synthetic.main.dialog_training_step.view.*

/**
 * TrainingStepDialog displays the dialog to edit and create new steps for trainings.
 */
class TrainingStepDialog(
    context: Context,
    private val viewGroup: ViewGroup,
    private val step: TrainingStep?,
    private val callback: (duration: String, power: String) -> Unit
) {
    private val dialogBuilder = AlertDialog.Builder(context)
    private val inflater = LayoutInflater.from(context)

    /**
     * Creates the dialog.
     */
    fun create() {
        val view = inflater.inflate(R.layout.dialog_training_step, viewGroup, false)
        step?.let {
            view.training_duration.setText(Format.formatDuration(it.duration))
            view.training_power.setText("${it.power}")
        }
        dialogBuilder.setView(view)
        dialogBuilder.setNegativeButton(android.R.string.no) { dialog, _ ->
            dialog.cancel()
        }
        dialogBuilder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            callback(view.training_duration.text.toString(), view.training_power.text.toString())
        }
    }

    /**
     * Shows the dialog.
     */
    fun show() {
        dialogBuilder.show()
    }
}
