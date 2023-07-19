package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

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
class SignInViewModel @Inject constructor(
    private val networkImpl: NetworkImpl
) : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val authorizationState: StateFlow<ApiStateUser> = _authorizationStateFlow

    fun authorizationUser(body: UserRequest) = viewModelScope.launch(Dispatchers.IO) {
        _authorizationStateFlow.value = ApiStateUser.Loading
        _authorizationStateFlow.value =
            networkImpl.authorizeUser(body)
    }
}