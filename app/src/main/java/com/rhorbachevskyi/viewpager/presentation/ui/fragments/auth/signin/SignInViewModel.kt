package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val authorizationState = _authorizationStateFlow.asStateFlow()

    fun authorizationUser(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        _authorizationStateFlow.value = ApiState.Loading
        _authorizationStateFlow.value = signInUseCase(email, password)
    }
}