package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class EditTextViewModel @Inject constructor(
    private val networkImpl: NetworkImpl
) : ViewModel() {
    private val _editUserStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val editUserState: StateFlow<ApiStateUser> = _editUserStateFlow
    fun requestEditUser(
        userId: Long,
        accessToken: String,
        name: String,
        career: String? = null,
        phone: String,
        address: String? = null,
        date: Date? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        _editUserStateFlow.value = ApiStateUser.Loading
        _editUserStateFlow.value =
            networkImpl.editUser(userId, accessToken, name, career, phone, address, date)
    }
}