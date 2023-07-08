package com.rhorbachevskyi.viewpager.ui.fragments.userprofile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditTextViewModel : ViewModel() {
    private val _editUserStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val editUserState: StateFlow<ApiState> = _editUserStateFlow
    fun requestEditUser(user: UserData, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _editUserStateFlow.value = ApiState.Loading
        NetworkImplementation.editUser(user, accessToken)
        _editUserStateFlow.value = NetworkImplementation.getStateEditUser()
    }
}