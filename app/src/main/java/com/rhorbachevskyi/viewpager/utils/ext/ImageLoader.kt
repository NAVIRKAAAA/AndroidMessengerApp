package com.rhorbachevskyi.viewpager.utils.ext


import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rhorbachevskyi.viewpager.R

fun ImageView.loadImage(image: String? = null) {
    Glide.with(this)
        .load(image)
        .centerCrop()
        .circleCrop()
        .placeholder(R.drawable.ic_user_photo)
        .error(
            Glide.with(this)
                .load(R.drawable.ic_user_photo)
                .centerCrop()
                .circleCrop()
        )
        .into(this)
}