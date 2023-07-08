package com.rhorbachevskyi.viewpager.ui.fragments.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val _getUserStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val getUserState: StateFlow<ApiState> = _getUserStateFlow
    fun requestGetUser(userId: Long, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _getUserStateFlow.value = ApiState.Loading
        NetworkImplementation.getUser(userId, accessToken)
        _getUserStateFlow.value = NetworkImplementation.getStateUser()
    }

}