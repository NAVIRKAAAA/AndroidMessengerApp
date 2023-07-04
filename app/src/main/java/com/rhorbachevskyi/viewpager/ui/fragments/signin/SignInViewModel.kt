package com.rhorbachevskyi.viewpager.ui.fragments.signin

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


class SignInViewModel : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
    val authorizationState: StateFlow<ApiState> = _authorizationStateFlow

    fun authorizationUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        val apiService = ApiServiceFactory.createApiService()

        try {
            val response = UserRepository(apiService).authorizeUser(body)
            _authorizationStateFlow.value =
                response.data?.let { ApiState.Success(it) } ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            _authorizationStateFlow.value = ApiState.Error(
                R.string.not_correct_input
            )
        }
    }
}