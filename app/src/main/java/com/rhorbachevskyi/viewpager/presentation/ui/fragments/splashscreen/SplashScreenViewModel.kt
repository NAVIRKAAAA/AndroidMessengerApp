package com.rhorbachevskyi.viewpager.presentation.ui.fragments.splashscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.repository.ContactRepository
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImplementation
import com.rhorbachevskyi.viewpager.data.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository,
    private val networkImpl: NetworkImplementation = NetworkImplementation(userRepository, contactRepository)
) : ViewModel() {
    private val _authorizationStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val authorizationState: StateFlow<ApiStateUser> = _authorizationStateFlow

    fun autoLogin(context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            _authorizationStateFlow.value = ApiStateUser.Loading
            _authorizationStateFlow.value = networkImpl.autoLogin(context)
        }
}