package com.rhorbachevskyi.viewpager.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID
@Parcelize
data class Contact(
    val name: String,
    val career: String,
    val photo: String = "",
    val id: UUID = UUID.randomUUID()
) : Parcelable {
    override fun toString(): String {
        return "Contact: id: $id, Full name: $name, Career: $career, Image: $photo"
    }
}
