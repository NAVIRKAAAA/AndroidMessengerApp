package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpExtendedViewModel @Inject constructor(
    private val networkImpl: NetworkImpl,
    ) : ViewModel() {
    private val _registerStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val registerState: StateFlow<ApiStateUser> = _registerStateFlow
    fun isLogout() {
            _registerStateFlow.value = ApiStateUser.Initial
    }

    fun registerUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _registerStateFlow.value = ApiStateUser.Loading
        _registerStateFlow.value = networkImpl.registerUser(body)
    }
}