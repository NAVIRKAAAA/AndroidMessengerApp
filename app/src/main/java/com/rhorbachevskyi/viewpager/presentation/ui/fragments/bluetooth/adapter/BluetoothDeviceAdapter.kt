package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.bluetooth.BluetoothDevice
import com.rhorbachevskyi.viewpager.databinding.ItemBluetoothDeviceBinding
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter.utils.DevicesDiffUtil

class BluetoothDeviceAdapter  :
    ListAdapter<BluetoothDevice, BluetoothDeviceAdapter.DevicesViewHolder>(DevicesDiffUtil()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DevicesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBluetoothDeviceBinding.inflate(inflater, parent, false)
        return DevicesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DevicesViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class DevicesViewHolder(private val binding: ItemBluetoothDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(device: BluetoothDevice) {
            with(binding) {
                textViewName.text = device.name
                textViewAddress.text = device.address
            }
        }

    }

}
