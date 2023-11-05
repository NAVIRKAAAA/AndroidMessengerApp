package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter.interfaces

import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.model.BluetoothDeviceDomain


interface DevicesClickListener {
    fun onDeviceClick(device : BluetoothDeviceDomain)
}