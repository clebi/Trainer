package com.clebi.trainer.ui.config

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.model.Device

/**
 * DeviceListAdapter is the list adapter for the search of trainers.
 */
class DeviceListAdapter(private var devices: List<Device>, private val addCallback: (device: Device) -> Unit) :
    RecyclerView.Adapter<DeviceListAdapter.DeviceListViewHolder>() {
    class DeviceListViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        /** the name of the device */
        val trainerSearchName = layout.findViewById<TextView>(R.id.trainer_search_name)!!
        /** the id of the device */
        val trainerSearchId = layout.findViewById<TextView>(R.id.trainer_search_id)!!
        /** the button to add the device to config. */
        val trainerSearchBtn = layout.findViewById<Button>(R.id.trainer_search_btn)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_search_list_item, parent, false)
        return DeviceListViewHolder(view as LinearLayout)
    }

    override fun getItemCount() = devices.count()

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val device = devices[position]
        holder.trainerSearchId.text = device.id
        holder.trainerSearchName.text = device.name
        holder.trainerSearchBtn.setOnClickListener {
            addCallback(device)
        }
    }

    /**
     * Push a new list of devices and refresh the list.
     */
    fun setDevices(devices: List<Device>) {
        this.devices = devices
        this.notifyDataSetChanged()
    }
}