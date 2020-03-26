package com.clebi.trainer.ui.trainings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.TrainingStep
import kotlinx.android.synthetic.main.training_step_item.view.*

/**
 * TrainingStepsListAdapter is responsible for display of a step in a list.
 */
class TrainingStepsListAdapter(steps: List<TrainingStep>) :
    RecyclerView.Adapter<TrainingStepsListAdapter.TrainingStepViewHolder>() {

    companion object {
        private const val TAG = "TrainingStepsListAdapter"
    }

    /** copy of steps (with mutability) we can modify live */
    private var steps = steps.toMutableList()

    class TrainingStepViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout)

    /**
     * TouchHelper manages drag and drop for steps.
     */
    inner class TouchHelper(private val callback: (from: Int, to: Int) -> Boolean) :
        ItemTouchHelper.Callback() {

        private var dragFrom = -1
        private var dragTo = -1

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPos = viewHolder.adapterPosition
            val toPos = target.adapterPosition
            Log.d(TAG, "from: $fromPos - to: $toPos")
            if (dragFrom == -1) {
                dragFrom = fromPos
            }
            dragTo = toPos
            if (fromPos < toPos) {
                for (i in fromPos until toPos) {
                    steps[i] = steps[i + 1].also { steps[i + 1] = steps[i] }
                }
            } else {
                for (i in fromPos downTo toPos + 1) {
                    steps[i] = steps[i - 1].also { steps[i - 1] = steps[i] }
                }
            }
            notifyItemMoved(fromPos, toPos)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        }

        /**
         * @see ItemTouchHelper.Callback
         * Calls the callback responsible fot the real modification on the trainings model.
         */
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            Log.d(TAG, "final drag from: $dragFrom - to: $dragTo")
            if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                callback(dragFrom, dragTo)
            }
        }
    }

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
        this.steps = steps.toMutableList()
        notifyDataSetChanged()
    }
}
