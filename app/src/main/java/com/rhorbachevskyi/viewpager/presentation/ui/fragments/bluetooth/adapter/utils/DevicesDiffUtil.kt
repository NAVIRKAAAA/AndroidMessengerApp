package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter.utils

import androidx.recyclerview.widget.DiffUtil
import com.rhorbachevskyi.viewpager.bluetooth.BluetoothDevice

class DevicesDiffUtil : DiffUtil.ItemCallback<BluetoothDevice>() {
    override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean = oldItem.address == newItem.address
}