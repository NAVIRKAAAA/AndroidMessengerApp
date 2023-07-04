package com.rhorbachevskyi.viewpager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    val id: Long, val name: String?, val email: String, val phone: String?, val career: String? = null,
    val address: String? = null, val birthday: String? = null, val facebook: String? = null, val instagram: String? = null,
    val twitter: String? = null, val linkedin: String? = null, val image: String? = null, val created_at: String? = null,
    val updated_at: String? = null
) : Parcelable