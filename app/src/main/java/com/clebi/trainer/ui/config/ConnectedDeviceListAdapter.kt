package com.clebi.trainer.ui.config

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectionStatusCallback
import com.clebi.trainer.devices.DeviceConnectionStatus

/**
 * DeviceListAdapter is the list adapter for the search of trainers.
 */
class ConnectedDeviceListAdapter(
    private var connectedDevices: List<ConnectedDevice>,
    private val statusNotConnectedStr: String,
    private val statusConnectedStr: String,
    private val statusConnectingStr: String
) :
    RecyclerView.Adapter<ConnectedDeviceListAdapter.DeviceListViewHolder>() {
    companion object {
        private const val TAG = "ConnectedDeviceListAdapter"
    }

    private val statusListeners = mutableListOf<ConnectionStatusCallback>()

    class DeviceListViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        /** the name of the device */
        val trainerName = layout.findViewById<TextView>(R.id.trainer_name)!!

        /** the id of the device */
        val trainerId = layout.findViewById<TextView>(R.id.trainer_id)!!

        /** status of the device */
        val trainerStatus = layout.findViewById<TextView>(R.id.trainer_status)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_config_item, parent, false)
        return DeviceListViewHolder(view as LinearLayout)
    }

    override fun getItemCount() = connectedDevices.count()

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val connectedDevice = connectedDevices[position]
        val statusTxt = when (connectedDevice.status) {
            DeviceConnectionStatus.CONNECTING -> statusConnectingStr
            DeviceConnectionStatus.CONNECTED -> statusConnectedStr
            else -> statusNotConnectedStr
        }
        holder.trainerId.text = connectedDevice.device.id
        holder.trainerName.text = connectedDevice.device.name
        holder.trainerStatus.text = statusTxt
    }

    /**
     * Push a new list of devices and refresh the list.
     */
    fun setDevices(devices: List<ConnectedDevice>) {
        connectedDevices = devices
        connectedDevices.forEachIndexed { index, connectedDevice ->
            if (statusListeners.count() > index) {
                connectedDevice.removeConnectionStatusListeners(statusListeners[index])
            }
            val callback = { status: DeviceConnectionStatus ->
                Log.d(TAG, "status changed: $status")
                notifyItemChanged(index)
            }
            statusListeners.add(callback)
            connectedDevice.addConnectionStatusListeners(callback)
        }
        this.notifyDataSetChanged()
    }
}
