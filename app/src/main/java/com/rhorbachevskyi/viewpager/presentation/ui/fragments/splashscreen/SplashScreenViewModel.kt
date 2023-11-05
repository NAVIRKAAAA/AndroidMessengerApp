package com.rhorbachevskyi.viewpager.presentation.ui.fragments.splashscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AutoSignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val autoSignInUseCase: AutoSignInUseCase
) : ViewModel() {
    private val _authorizationState = MutableStateFlow<ApiState>(ApiState.Initial)
    val authorizationState: StateFlow<ApiState> = _authorizationState



    fun autoLogin(context: Context) =
        viewModelScope.launch(Dispatchers.IO) {
            _authorizationState.value = ApiState.Loading
            _authorizationState.value = autoSignInUseCase(context)
        }


}