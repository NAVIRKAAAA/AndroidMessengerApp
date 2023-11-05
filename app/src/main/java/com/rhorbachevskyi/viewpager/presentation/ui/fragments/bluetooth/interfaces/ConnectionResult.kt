package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.interfaces

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
}