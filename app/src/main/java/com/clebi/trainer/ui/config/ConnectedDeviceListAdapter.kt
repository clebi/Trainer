package com.clebi.trainer.ui.config

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.R

/**
 * DeviceListAdapter is the list adapter for the search of trainers.
 */
class ConnectedDeviceListAdapter(private var connectedDevices: List<ConnectedDevice>) :
    RecyclerView.Adapter<ConnectedDeviceListAdapter.DeviceListViewHolder>() {
    class DeviceListViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        /** the name of the device */
        val trainerSearchName = layout.findViewById<TextView>(R.id.trainer_search_name)!!

        /** the id of the device */
        val trainerSearchId = layout.findViewById<TextView>(R.id.trainer_search_id)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_search_config_item, parent, false)
        return DeviceListViewHolder(view as LinearLayout)
    }

    override fun getItemCount() = connectedDevices.count()

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val connectedDevice = connectedDevices[position]
        holder.trainerSearchId.text = connectedDevice.device.id
        holder.trainerSearchName.text = connectedDevice.device.name
    }

    /**
     * Push a new list of devices and refresh the list.
     */
    fun setDevices(devices: List<ConnectedDevice>) {
        this.connectedDevices = devices
        this.notifyDataSetChanged()
    }
}