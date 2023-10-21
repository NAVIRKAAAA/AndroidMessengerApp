package com.rhorbachevskyi.viewpager.domain.states

sealed class ApiState {
    object Initial : ApiState()
    data class Success<T>(val data: T) : ApiState()
    data class Error(val error: Int) : ApiState()
    object Loading : ApiState()
}