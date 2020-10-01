package com.clebi.trainer.ui.config

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clebi.trainer.R
import com.clebi.trainer.devices.ConnectedDevice
import com.clebi.trainer.devices.ConnectedDeviceListener
import com.clebi.trainer.devices.DeviceCapability
import com.clebi.trainer.devices.DeviceConnectionStatus

/**
 * DeviceListAdapter is the list adapter for the search of trainers.
 */
class ConnectedDeviceListAdapter(
    private var connectedDevices: List<ConnectedDevice>
) :
    RecyclerView.Adapter<ConnectedDeviceListAdapter.DeviceListViewHolder>() {

    private val deviceListeners = mutableListOf<ConnectedDeviceListener>()

    class DeviceListViewHolder(layout: LinearLayout) : RecyclerView.ViewHolder(layout) {
        /** the name of the device */
        val trainerName = layout.findViewById<TextView>(R.id.trainer_name)!!

        /** the id of the device */
        val trainerId = layout.findViewById<TextView>(R.id.trainer_id)!!

        /** status of the device */
        val trainerStatus = layout.findViewById<TextView>(R.id.trainer_status)!!

        /** Capabilities of the device */
        val trainerCapabilities = layout.findViewById<TextView>(R.id.trainer_capa)!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.trainer_config_item, parent, false)
        return DeviceListViewHolder(view as LinearLayout)
    }

    override fun getItemCount() = connectedDevices.count()

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        val resources = holder.itemView.resources
        val connectedDevice = connectedDevices[position]
        val statusTxt = when (connectedDevice.status) {
            DeviceConnectionStatus.CONNECTING -> resources.getString(R.string.trainer_status_connecting)
            DeviceConnectionStatus.CONNECTED -> resources.getString(R.string.trainer_status_connected)
            else -> resources.getString(R.string.trainer_status_not_connected)
        }
        holder.trainerId.text = connectedDevice.device.id
        holder.trainerName.text = connectedDevice.device.name
        holder.trainerStatus.text = statusTxt
        if (connectedDevice.capabilities.contains(DeviceCapability.BIKE_TRAINER)) {
            holder.trainerCapabilities.text = resources.getString(R.string.trainer_capa_bike_trainer)
        } else if (connectedDevice.capabilities.contains(DeviceCapability.BIKE_POWER)) {
            holder.trainerCapabilities.text = resources.getString(R.string.trainer_capa_bike_power)
        } else {
            holder.trainerCapabilities.text = resources.getString(R.string.trainer_capa_unknown)
        }
    }

    /**
     * Push a new list of devices and refresh the list.
     */
    fun setDevices(devices: List<ConnectedDevice>) {
        connectedDevices = devices
        connectedDevices.forEachIndexed { index, connectedDevice ->
            if (deviceListeners.count() > index) {
                connectedDevice.removeListener(deviceListeners[index])
            }
            val listener = { _: ConnectedDevice ->
                notifyItemChanged(index)
            }
            connectedDevice.addListener(listener)
            deviceListeners.add(listener)
        }
        this.notifyDataSetChanged()
    }
}
