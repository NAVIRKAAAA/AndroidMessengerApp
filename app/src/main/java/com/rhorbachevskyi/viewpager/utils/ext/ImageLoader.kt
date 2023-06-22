package com.rhorbachevskyi.viewpager.utils.ext


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rhorbachevskyi.viewpager.R

fun ImageView.loadImage(image: String) {
    Glide.with(this)
        .load(image)
        .circleCrop()
        .placeholder(R.drawable.ic_user_photo)
        .error(R.drawable.ic_user_photo)
        .into(this)
}