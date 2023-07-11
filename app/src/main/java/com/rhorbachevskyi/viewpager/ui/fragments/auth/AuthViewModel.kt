package com.rhorbachevskyi.viewpager.ui.fragments.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _registerStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val registerState: StateFlow<ApiState> = _registerStateFlow
    fun isLogout() {
        _registerStateFlow.value = ApiState.Loading
    }
    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _registerStateFlow.value = ApiState.Loading
        NetworkImplementation.registerUser(body)
        _registerStateFlow.value = NetworkImplementation.getStateRegister()
    }
}