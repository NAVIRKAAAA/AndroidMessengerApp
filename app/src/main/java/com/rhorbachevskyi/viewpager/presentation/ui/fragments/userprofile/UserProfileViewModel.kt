package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import androidx.lifecycle.ViewModel
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor() : ViewModel() {
    private val _getUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val getUserState: StateFlow<ApiStateUser> = _getUserStateFlow
    fun requestGetUser() {
        _getUserStateFlow.value = ApiStateUser.Loading
        _getUserStateFlow.value = ApiStateUser.Success(UserDataHolder.getUserData())
    }
}