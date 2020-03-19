package com.clebi.trainer.ui.trainings

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.trainings.Training
import kotlinx.android.synthetic.main.training_item.view.*

/**
 * TrainingsListAdapter is the adapter for trainings list.
 */
class TrainingsListAdapter(private var trainingsList: List<Training>) :
    RecyclerView.Adapter<TrainingsListAdapter.TrainingViewHolder>() {

    companion object {
        private const val TAG = "TrainingsListAdapter"
    }

    class TrainingViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout)

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
        holder.itemView.duration.text = "00:00:00"
    }
}
