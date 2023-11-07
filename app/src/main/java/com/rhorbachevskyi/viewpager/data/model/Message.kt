package com.rhorbachevskyi.viewpager.data.model

import com.google.gson.annotations.SerializedName



data class Message(
    @SerializedName("active") val active: Boolean = false,
    @SerializedName("senderId") val senderId: Long = 0,
    @SerializedName("receiverId") val receiverId: Long = 0,
    @SerializedName("text") val text: String = ""
)
