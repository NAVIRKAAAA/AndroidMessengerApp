package com.rhorbachevskyi.viewpager.ui.fragments.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.ui.fragments.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SignInViewModel : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<AuthViewModel.RegisterState>(AuthViewModel.RegisterState.Loading)
    val authorizationState: StateFlow<AuthViewModel.RegisterState> = _authorizationStateFlow

    fun authorizationUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {

    }
}