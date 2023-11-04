package com.rhorbachevskyi.viewpager.bluetooth

sealed interface ConnectionResult {
    object ConnectionEstablished: ConnectionResult
    data class Error(val message: String): ConnectionResult
}