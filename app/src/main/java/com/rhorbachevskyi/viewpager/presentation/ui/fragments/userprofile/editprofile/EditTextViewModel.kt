package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditTextViewModel : ViewModel() {
    private val _editUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val editUserState: StateFlow<ApiStateUser> = _editUserStateFlow
    fun requestEditUser(user: UserData, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _editUserStateFlow.value = ApiStateUser.Loading
        NetworkImplementation.editUser(user, accessToken)
        _editUserStateFlow.value = NetworkImplementation.getStateEditUser()
    }
}