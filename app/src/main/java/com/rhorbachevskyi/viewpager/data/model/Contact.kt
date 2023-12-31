package com.rhorbachevskyi.viewpager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val name: String? = null,
    val career: String? = null,
    val photo: String? = null,
    val address: String? = null,
    val id: Long = 0,
) : Parcelable
