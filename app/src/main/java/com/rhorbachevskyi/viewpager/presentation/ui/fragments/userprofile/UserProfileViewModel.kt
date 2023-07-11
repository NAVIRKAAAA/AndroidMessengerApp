package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {
    private val _getUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val getUserState: StateFlow<ApiStateUser> = _getUserStateFlow
    fun requestGetUser(userId: Long, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _getUserStateFlow.value = ApiStateUser.Loading
        NetworkImplementation.getUser(userId, accessToken)
        _getUserStateFlow.value = NetworkImplementation.getStateUser()
    }

}