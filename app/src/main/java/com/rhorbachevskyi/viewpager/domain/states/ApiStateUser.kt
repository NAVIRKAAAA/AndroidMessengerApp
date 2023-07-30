package com.rhorbachevskyi.viewpager.domain.states

sealed class ApiStateUser {
    object Initial : ApiStateUser()
    data class Success<T>(val data: T) : ApiStateUser()
    data class Error(val error: Int) : ApiStateUser()
    object Loading : ApiStateUser()
}