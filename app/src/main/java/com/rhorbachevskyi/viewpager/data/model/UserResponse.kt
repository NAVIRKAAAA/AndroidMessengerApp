package com.rhorbachevskyi.viewpager.data.model

data class UserResponse(
    val status: String? = null,
    val code: Int = 200,
    val message: String? = null,
    val data: Data? = null
) {
    data class Data(val user: UserData, val accessToken: String, val refreshToken: String)
}
