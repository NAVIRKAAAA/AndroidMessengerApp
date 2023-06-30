package com.rhorbachevskyi.viewpager.data.model

import android.service.autofill.UserData
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("status") var status: String? = null,
    @SerializedName("code") var code: Int? = null,
    @SerializedName("message") var message: String? = null,
    @SerializedName("data") var data: UserData? = null
)
