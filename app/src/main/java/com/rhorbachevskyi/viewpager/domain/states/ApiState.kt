package com.rhorbachevskyi.viewpager.domain.states

sealed class ApiState {
    data object Initial : ApiState()
    data class Success<T>(val data: T) : ApiState()
    data class Error(val error: String) : ApiState()
    data object Loading : ApiState()
}