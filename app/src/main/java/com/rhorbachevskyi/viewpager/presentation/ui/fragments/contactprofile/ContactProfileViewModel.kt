package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.domain.useCases.AddContactUseCase
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

    private val _usersStateFlow = MutableStateFlow<ApiStateUser>(ApiStateUser.Initial)
    val usersState: StateFlow<ApiStateUser> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contact: Contact, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if(!hasInternet) {
                _usersStateFlow.value = ApiStateUser.Error(R.string.No_internet_connection)
                return@launch
            }
            if (alreadyAdded) return@launch
            alreadyAdded = true

            _usersStateFlow.value = ApiStateUser.Loading
            _usersStateFlow.value =
                addContactUseCase(
                    userId,
                    contact,
                    accessToken
                )
        }
    fun changeState() {
        _usersStateFlow.value = ApiStateUser.Initial
    }

    fun requestGetUser(): UserResponse.Data = UserDataHolder.userData
}