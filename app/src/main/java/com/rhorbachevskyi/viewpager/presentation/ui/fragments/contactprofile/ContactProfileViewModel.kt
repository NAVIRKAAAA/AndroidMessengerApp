package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.domain.usecases.AddContactUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(
    private val addContactUseCase: AddContactUseCase
) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiState>(ApiState.Initial)
    val usersState: StateFlow<ApiState> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contactId: Long, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!hasInternet) {
                _usersStateFlow.value = ApiState.Error("not has internet hahaha")
                return@launch
            }
            if (alreadyAdded) return@launch
            alreadyAdded = true

            _usersStateFlow.value = ApiState.Loading
            _usersStateFlow.value =
                addContactUseCase(
                    accessToken,
                    userId,
                    contactId,
                )
        }

    fun changeState() {
        _usersStateFlow.value = ApiState.Initial
    }

    fun requestGetUser(): UserResponse.Data = UserDataHolder.userData
}