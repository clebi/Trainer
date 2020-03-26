package com.clebi.trainer.ui.trainings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Format
import com.clebi.trainer.trainings.Training
import kotlinx.android.synthetic.main.training_item.view.*

typealias OnClickListener = (position: Int) -> Unit
typealias OnDeleteListener = (position: Int) -> Unit

/**
 * TrainingsListAdapter is the adapter for trainings list.
 */
class TrainingsListAdapter(
    private var trainingsList: List<Training>,
    private val onClickListener: OnClickListener,
    private val onDeleteListener: OnDeleteListener
) :
    RecyclerView.Adapter<TrainingsListAdapter.TrainingViewHolder>() {

    companion object {
        private const val TAG = "TrainingsListAdapter"
    }

    class TrainingViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        fun bind(position: Int, listener: OnClickListener) {
            itemView.setOnClickListener {
                listener(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.training_item, parent, false)
        return TrainingViewHolder(view as LinearLayout)
    }

    override fun getItemCount(): Int {
        return trainingsList.count()
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = trainingsList[position]
        Log.d(TAG, "item: $training")
        holder.itemView.name.text = training.name
        val duration: Int = training.steps.stream().mapToInt {
            it.duration
        }.sum()
        holder.itemView.duration.text = Format.formatDuration(duration)
        holder.itemView.training_delete_btn.setOnClickListener {
            onDeleteListener(position)
        }
        holder.bind(position, onClickListener)
    }

    fun setTrainings(trainings: List<Training>) {
        trainingsList = trainings
        notifyDataSetChanged()
    }
}
