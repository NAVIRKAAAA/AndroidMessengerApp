package com.rhorbachevskyi.viewpager.data.model

data class ContactsResponse(
    val status: String,
    val code: String,
    val message: String?,
    val data: Data
) {
    data class Data(val contacts: List<UserData>?)
}



