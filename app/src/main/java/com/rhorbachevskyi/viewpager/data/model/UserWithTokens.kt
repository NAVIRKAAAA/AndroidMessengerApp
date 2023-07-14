package com.rhorbachevskyi.viewpager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserWithTokens(val user: UserData, val accessToken: String, val refreshToken: String) : Parcelable
