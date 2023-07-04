package com.rhorbachevskyi.viewpager.data.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("phone") val phone: String? = null,
)
