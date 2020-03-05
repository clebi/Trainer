package com.clebi.trainer.ui.config

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.clebi.trainer.R
import com.clebi.trainer.WahooTrainerService
import com.clebi.trainer.model.NetworkState
import com.clebi.trainer.model.NetworkType

/**
 * TrainerConfigFragment is responsible for the trainers configuration
 * and is responsible for launching the search of new trainers.
 * It shall manage the known devices.
 */
class TrainerConfigFragment : Fragment() {

    /** The trainer service */
    private lateinit var configService: WahooTrainerService

    /**
     * Listener for network changes,
     * responsible for showing the bluetooth disabled warning and disabling the search button.
     */
    private val networkChangeListener = { networkType: NetworkType, networkState: NetworkState ->
        Log.d(
            "TrainerConfigFragment",
            "$networkType - $networkState"
        )
        when (networkType) {
            NetworkType.BLUETOOTH -> {
                if (networkState == NetworkState.ENABLED) {
                    view!!.findViewById<TextView>(R.id.warningTxt).visibility = View.GONE
                    view!!.findViewById<Button>(R.id.trainer_search_btn).isEnabled = true
                } else {
                    view!!.findViewById<TextView>(R.id.warningTxt).visibility = View.VISIBLE
                    view!!.findViewById<Button>(R.id.trainer_search_btn).isEnabled = false
                }
            }
        }
        Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configService = WahooTrainerService.instance
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trainer_config, container, false)
        val searchBtn = view.findViewById<Button>(R.id.trainer_search_btn)
        searchBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.trainerSearchFragment))
        return view
    }

    override fun onResume() {
        super.onResume()
        configService.listenNetworkChange(networkChangeListener)
    }

    override fun onPause() {
        super.onPause()
        configService.unlistenNetworkChange(networkChangeListener)
    }
}
