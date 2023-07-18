package com.rhorbachevskyi.viewpager.domain.states

import com.rhorbachevskyi.viewpager.data.model.UserResponse

sealed class ApiStateUser {
    object Initial : ApiStateUser()
    data class Success(val userData: UserResponse.Data) : ApiStateUser()
    data class Error(val error: Int) : ApiStateUser()
    object Loading : ApiStateUser()
}