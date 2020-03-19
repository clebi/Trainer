package com.clebi.trainer.ui.trainings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebi.trainer.R
import com.clebi.trainer.devices.wahoo.WahooTrainerService
import com.clebi.trainer.trainings.Training
import kotlinx.android.synthetic.main.fragment_trainings.view.*

/**
 * TrainingsFragment manages the trainings.
 */
class TrainingsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Intent(context, WahooTrainerService::class.java).also {
            activity?.startService(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_trainings, container, false)
        view.trainings_list.apply {
            adapter = TrainingsListAdapter(listOf(Training("Test1"), Training("Test2")))
            layoutManager = LinearLayoutManager(context)
        }
        return view
    }
}
