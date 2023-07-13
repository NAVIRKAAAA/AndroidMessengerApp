package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpExtendedViewModel : ViewModel() {
    private val _registerStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val registerState: StateFlow<ApiStateUser> = _registerStateFlow
    fun isLogout() {
        _registerStateFlow.value = ApiStateUser.Loading
    }
    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _registerStateFlow.value = ApiStateUser.Loading
        NetworkImplementation.registerUser(body)
        _registerStateFlow.value = NetworkImplementation.getStateRegister()
    }
}