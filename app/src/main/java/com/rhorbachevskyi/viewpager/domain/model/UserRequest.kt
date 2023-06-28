package com.rhorbachevskyi.viewpager.domain.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null,
)
