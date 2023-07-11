package com.rhorbachevskyi.viewpager.presentation.utils.ext

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.gone() {
    visibility = View.GONE
}
fun View.visibleIf(condition: Boolean) {
    if(condition) visible() else invisible()
}
fun View.showErrorSnackBar(context : Context, error: Int){
    val errorString = context.getString(error)
    Snackbar.make(
        this,
        errorString,
        Snackbar.LENGTH_SHORT
    ).show()
}
