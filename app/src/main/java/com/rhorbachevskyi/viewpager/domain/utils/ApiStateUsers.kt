package com.rhorbachevskyi.viewpager.domain.utils

import com.rhorbachevskyi.viewpager.data.model.UserData

sealed class ApiStateUsers {
    object Initial : ApiStateUsers()
    data class Success(val data: ArrayList<UserData>?) : ApiStateUsers()
    data class Error(val error: Int) : ApiStateUsers()
    object Loading : ApiStateUsers()
}