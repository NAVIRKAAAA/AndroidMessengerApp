package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import com.rhorbachevskyi.viewpager.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _getUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val getUserState: StateFlow<ApiStateUser> = _getUserStateFlow
    fun requestGetUser(userId: Long, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _getUserStateFlow.value = ApiStateUser.Loading
        _getUserStateFlow.value = NetworkImplementation(userRepository).getUser(userId, accessToken)
    }

}