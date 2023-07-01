package com.rhorbachevskyi.viewpager.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID
@Parcelize
data class Contact(
    val name: String,
    val career: String,
    val photo: String = "",
    val id: UUID = UUID.randomUUID(),
    var isChecked: Boolean = false

) : Parcelable