package com.rhorbachevskyi.viewpager.ui.fragments.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.utils.ApiServiceFactory
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel : ViewModel() { // TODO: register exception
    private val _registerStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val registerState: StateFlow<ApiState> = _registerStateFlow
    fun isLogout() {
        _registerStateFlow.value = ApiState.Loading
    }
    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _registerStateFlow.value = ApiState.Loading

        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).registerUser(body)
            _registerStateFlow.value =
                response.data?.let { ApiState.Success(it) } ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            _registerStateFlow.value = ApiState.Error(
                R.string.register_error_user_exist
            )
        }
    }
}