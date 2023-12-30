package com.rhorbachevskyi.viewpager.presentation.utils.ext

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackBar(text: String, actionText: String = "", action: () -> Unit = {}) {
    Snackbar.make(
        this.requireView(),
        text,
        Snackbar.LENGTH_LONG
    ).setAction(actionText) {
        action()
    }.show()
}

fun Context.hasInternet(): Boolean {

    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false

    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}