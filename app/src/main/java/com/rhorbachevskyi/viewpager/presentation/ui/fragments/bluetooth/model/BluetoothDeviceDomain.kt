package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.model

typealias BluetoothDeviceDomain = BluetoothDevice
data class BluetoothDevice(
    val name: String?,
    val address: String
)