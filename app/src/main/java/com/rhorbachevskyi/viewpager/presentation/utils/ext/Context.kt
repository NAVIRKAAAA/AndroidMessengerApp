package com.rhorbachevskyi.viewpager.presentation.utils.ext

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R

fun Context.showSnackBar(view: View, text: String, action: () -> Unit = {}) {
    Snackbar.make(
        view,
        text,
        Snackbar.LENGTH_LONG
    ).setAction(getString(R.string.restore)) {
        action()
    }.show()
}