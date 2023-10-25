package com.rhorbachevskyi.viewpager.presentation.utils.ext


import android.widget.ImageView
import androidx.annotation.DrawableRes
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
                .load(R.drawable.ic_avatar)
                .centerCrop()
                .circleCrop()
        )
        .into(this)
}

fun ImageView.loadImage(@DrawableRes image: Int) {
    Glide.with(this)
        .load(image)
        .centerCrop()
        .circleCrop()
        .placeholder(R.drawable.ic_user_photo)
        .error(
            Glide.with(this)
                .load(R.drawable.ic_avatar)
                .centerCrop()
                .circleCrop()
        )
        .into(this)
}