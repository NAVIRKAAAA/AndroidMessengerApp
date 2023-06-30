package com.rhorbachevskyi.viewpager.data.model

import android.service.autofill.UserData
import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("users") var userData: ArrayList<UserData> = arrayListOf()
)
