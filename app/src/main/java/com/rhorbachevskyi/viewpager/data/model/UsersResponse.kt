package com.rhorbachevskyi.viewpager.data.model


data class UsersResponse(
    val status: String,
    val code: String,
    val message: String?,
    val data: Data
) {
    data class Data(val users: List<UserData>?)
}
