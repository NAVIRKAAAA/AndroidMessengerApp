package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter

import com.rhorbachevskyi.viewpager.bluetooth.BluetoothDeviceDomain

interface DevicesClickListener { // TODO: impl
    fun onDeviceClick(): BluetoothDeviceDomain
}