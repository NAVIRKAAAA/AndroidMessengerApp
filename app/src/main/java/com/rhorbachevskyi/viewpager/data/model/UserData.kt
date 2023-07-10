package com.rhorbachevskyi.viewpager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class UserData(
    val id: Long,
    val name: String? = null,
    val email: String,
    val phone: String? = null,
    val career: String? = null,
    val address: String? = null,
    val birthday: Date? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val twitter: String? = null,
    val linkedin: String? = null,
    val image: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
) : Parcelable {
    fun toContact(): Contact = Contact(name, career, image, address, id)
}