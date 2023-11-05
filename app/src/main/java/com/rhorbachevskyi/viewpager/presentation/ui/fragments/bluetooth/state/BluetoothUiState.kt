package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.state

import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.model.BluetoothDevice

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
)