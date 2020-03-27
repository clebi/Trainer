package com.clebi.trainer.ui.config

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.devices.Device
import com.clebi.trainer.devices.wahoo.WahooTrainerService

/**
 * TrainerSearchFragment manages the search of training devices.
 */
class TrainerSearchFragment : Fragment() {
    companion object {
        const val TAG = "TrainerSearchFragment"
    }

    private lateinit var configService: WahooTrainerService
    private lateinit var trainerSearchListView: RecyclerView
    private val devicesConfigModel: DevicesConfigModel by activityViewModels()

    private val discoveredListener = { device: Device ->
        Log.d(TAG, "device discovered: $device")
        devicesConfigModel.addSearchDevice(device)
        Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configService = WahooTrainerService.instance
        devicesConfigModel.resetSearchDevices()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trainer_search, container, false)
        val trainerSearchListAdapter = DeviceListAdapter(devicesConfigModel.searchDevices.value!!) { device ->
            Log.d(TAG, "device add: ${device.name} - ${device.id}")
            if (devicesConfigModel.connectedDevices.value!!.filter {
                    it.device.id == device.id
                }.count() > 0) {
                Log.d(TAG, "device already exists: ${device.name} - ${device.id}")
                val toast = Toast.makeText(
                    context,
                    resources.getString(R.string.training_device_already_exists),
                    Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.TOP, 0, 25)
                toast.show()
                return@DeviceListAdapter
            }
            val connectedDevice = configService.connectToDevice(device)
            devicesConfigModel.addConnectedDevices(connectedDevice)
            findNavController().popBackStack()
        }
        trainerSearchListView = view.findViewById<RecyclerView>(R.id.trainers_search_list).apply {
            adapter = trainerSearchListAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
        devicesConfigModel.searchDevices.observe(viewLifecycleOwner, Observer {
            trainerSearchListAdapter.setDevices(it)
        })
        return view
    }

    override fun onResume() {
        super.onResume()
        configService.listenDevicesDiscovery(discoveredListener)
        configService.searchForDevices()
    }

    override fun onPause() {
        super.onPause()
        configService.stopSearchForDevices()
        configService.unlistenDevicesDiscovery(discoveredListener)
    }
}
