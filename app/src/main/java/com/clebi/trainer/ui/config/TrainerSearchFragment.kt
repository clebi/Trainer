package com.clebi.trainer.ui.config

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.WahooTrainerService

/**
 * TrainerSearchFragment manages the search of training devices.
 */
class TrainerSearchFragment : Fragment() {
    companion object {
        const val TAG = "TrainerSearchFragment"
    }

    private lateinit var configService: WahooTrainerService
    private lateinit var trainerSearchListView: RecyclerView
    private lateinit var configModel: ConfigModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configModel = ViewModelProvider(this).get(ConfigModel::class.java)
        configService = WahooTrainerService.instance
        configService.listenDevicesDiscovery {
            Log.d(TAG, "device discovered: $it")
            configModel.addDevice(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trainer_search, container, false)
        val trainerSearchListAdapter = DeviceListAdapter(configModel.devices.value!!) { device ->
            Log.d(TAG, "device add: ${device.name} - ${device.id}")
        }
        trainerSearchListView = view.findViewById<RecyclerView>(R.id.trainers_search_list).apply {
            adapter = trainerSearchListAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
        configModel.devices.observe(viewLifecycleOwner, Observer {
            trainerSearchListAdapter.setDevices(it)
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        configService.searchForDevices()
    }

    override fun onPause() {
        super.onPause()
        configService.stopSearchForDevices()
    }
}