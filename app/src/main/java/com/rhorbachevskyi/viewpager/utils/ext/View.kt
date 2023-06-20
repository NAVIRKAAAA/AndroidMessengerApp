package com.rhorbachevskyi.viewpager.utils.ext

import android.view.View
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}
fun View.visibleIf(condition: Boolean) {
    if(condition) visible() else invisible()
}