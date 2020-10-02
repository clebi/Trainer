package com.clebi.trainer.ui.config

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.TrainerApp
import com.clebi.trainer.devices.NetworkState
import com.clebi.trainer.devices.NetworkType
import com.clebi.trainer.devices.TrainerService

/**
 * TrainerConfigFragment is responsible for the trainers configuration
 * and is responsible for launching the search of new trainers.
 * It shall manage the known devices.
 */
class TrainerConfigFragment : Fragment() {

    /** The trainer service */
    private lateinit var configService: TrainerService

    private val devicesConfigModel: DevicesConfigModel by activityViewModels()

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
                    requireView().findViewById<TextView>(R.id.warningTxt).visibility = View.GONE
                    requireView().findViewById<Button>(R.id.trainer_search_btn).isEnabled = true
                } else {
                    requireView().findViewById<TextView>(R.id.warningTxt).visibility = View.VISIBLE
                    requireView().findViewById<Button>(R.id.trainer_search_btn).isEnabled = false
                }
            }
        }
        Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configService = TrainerService.instance
        val devices = devicesConfigModel.readConnectedDevicesFromStorage()
        devices.filter {
            it.provider == configService.provider()
        }.forEach {
            val connectedDevice = configService.connectToDevice(it)
            devicesConfigModel.addConnectedDevices(connectedDevice)
            (requireContext().applicationContext as TrainerApp).devices = devicesConfigModel.connectedDevices.value
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trainer_config, container, false)
        val searchBtn = view.findViewById<Button>(R.id.trainer_search_btn)
        searchBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.trainerSearchFragment))
        val connectedDeviceListAdapter = ConnectedDeviceListAdapter(devicesConfigModel.connectedDevices.value!!)
        view.findViewById<RecyclerView>(R.id.connected_devices).apply {
            adapter = connectedDeviceListAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
        devicesConfigModel.connectedDevices.observe(
            viewLifecycleOwner,
            {
                (requireContext().applicationContext as TrainerApp).devices = it
            }
        )
        devicesConfigModel.connectedDevices.observe(
            viewLifecycleOwner,
            {
                connectedDeviceListAdapter.setDevices(it)
            }
        )
        return view
    }

    override fun onResume() {
        super.onResume()
        configService.listenNetworkChange(networkChangeListener)
    }

    override fun onPause() {
        super.onPause()
        configService.unlistenNetworkChange(networkChangeListener)
        devicesConfigModel.saveConnectedDevicesToStorage()
    }
}
