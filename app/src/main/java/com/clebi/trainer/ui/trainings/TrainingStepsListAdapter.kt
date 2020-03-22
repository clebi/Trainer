package com.clebi.trainer.ui.trainings

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.TrainingStep
import kotlinx.android.synthetic.main.training_step_item.view.*

/**
 * TrainingStepsListAdapter is responsible for display of a step in a list.
 */
class TrainingStepsListAdapter(private var steps: List<TrainingStep>) :
    RecyclerView.Adapter<TrainingStepsListAdapter.TrainingStepViewHolder>() {

    class TrainingStepViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingStepViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.training_step_item, parent, false)
        return TrainingStepViewHolder(view as LinearLayout)
    }

    override fun getItemCount(): Int {
        return steps.count()
    }

    override fun onBindViewHolder(holder: TrainingStepViewHolder, position: Int) {
        val step = steps[position]
        holder.itemView.step_id.text =
            holder.itemView.resources.getText(R.string.training_step_num).toString().format(position + 1)
        holder.itemView.step_duration.text = Format.formatDuration(step.duration)
        holder.itemView.step_power.text = "${step.power}W"
    }

    /**
     * Set new training steps list.
     * @param steps new training steps.
     */
    fun setTrainingSteps(steps: List<TrainingStep>) {
        this.steps = steps
        notifyDataSetChanged()
    }
}
