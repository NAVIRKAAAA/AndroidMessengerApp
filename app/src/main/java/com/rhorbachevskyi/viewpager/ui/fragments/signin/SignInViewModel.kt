package com.rhorbachevskyi.viewpager.ui.fragments.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SignInViewModel : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val authorizationState: StateFlow<ApiState> = _authorizationStateFlow

    fun authorizationUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _authorizationStateFlow.value = ApiState.Loading
        NetworkImplementation.authorizationUser(body)
        _authorizationStateFlow.value = NetworkImplementation.getStateLogin()
    }
    fun autoLogin(context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            _authorizationStateFlow.value = ApiState.Loading
            NetworkImplementation.autoLogin(context)
            _authorizationStateFlow.value = NetworkImplementation.getStateAutoLogin()
        }
}