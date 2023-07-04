package com.rhorbachevskyi.viewpager.domain.utils

import com.rhorbachevskyi.viewpager.data.model.UserResponse

sealed class ApiState {
    data class Success(val userData: UserResponse.Data) : ApiState()
    data class Error(val error: Int) : ApiState()
    object Loading : ApiState()
}