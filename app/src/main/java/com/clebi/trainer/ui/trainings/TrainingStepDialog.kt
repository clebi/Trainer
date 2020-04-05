package com.clebi.trainer.ui.trainings

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
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
    private val view = inflater.inflate(R.layout.dialog_training_step, viewGroup, false)
    private val errors: MutableMap<String, Boolean> = mutableMapOf()
    private lateinit var dialog: AlertDialog

    /**
     * Creates the dialog.
     */
    fun create() {
        step?.let {
            view.training_duration.setText(Format.formatShortDuration(it.duration))
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
        dialog = dialogBuilder.create() as AlertDialog
    }

    /**
     * Shows the dialog.
     */
    fun show() {
        dialog.show()
        // The positive button is disabled till training name is correct
        val okBtn = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        errorDuration(true)
        errorPower(true)
        hasErrors(okBtn)
        view.training_duration.addTextChangedListener { text: Editable? ->
            if (text.isNullOrEmpty() || text.isBlank() || text.length < 3) {
                errorDuration(true)
                hasErrors(okBtn)
            } else {
                try {
                    Format.shortDurationFromString(text.toString())
                    errorDuration(false)
                    hasErrors(okBtn)
                } catch (_: IllegalArgumentException) {
                    errorDuration(true)
                    hasErrors(okBtn)
                }
            }
        }
        view.training_power.addTextChangedListener { text: Editable? ->
            if (text.isNullOrEmpty() || text.isBlank() || !text.isDigitsOnly()) {
                errorPower(true)
                hasErrors(okBtn)
            } else {
                errorPower(false)
                hasErrors(okBtn)
            }
        }
    }

    private fun hasErrors(button: Button) {
        button.isEnabled = errors.filter { entry -> entry.value }.count() <= 0
    }

    private fun errorDuration(isError: Boolean) {
        errors["duration"] = isError
        if (isError) {
            view.training_duration.error = viewGroup.resources.getString(R.string.training_duration_error)
        } else {
            view.training_duration.error = null
        }
    }

    private fun errorPower(isError: Boolean) {
        errors["power"] = isError
        if (isError) {
            view.training_power.error = viewGroup.resources.getString(R.string.training_power_error)
        } else {
            view.training_power.error = null
        }
    }
}
