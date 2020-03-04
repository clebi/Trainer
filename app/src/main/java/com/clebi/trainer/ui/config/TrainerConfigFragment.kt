package com.clebi.trainer.ui.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.clebi.trainer.R
import com.clebi.trainer.WahooTrainerService

/**
 * TrainerConfigFragment is responsible for the trainers configuration
 * and is responsible for launching the search of new trainers.
 * It shall manage the known devices.
 */
class TrainerConfigFragment : Fragment() {

    private lateinit var configService: WahooTrainerService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configService = WahooTrainerService.instance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trainer_config, container, false)
        val searchBtn = view.findViewById<Button>(R.id.trainer_search_btn)
        searchBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.trainerSearchFragment))
        return view
    }
}
