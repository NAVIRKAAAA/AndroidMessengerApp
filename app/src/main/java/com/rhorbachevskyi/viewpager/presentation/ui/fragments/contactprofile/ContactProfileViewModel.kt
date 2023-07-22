package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(
    private val networkImpl: NetworkImpl
) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contact: Contact, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if(!hasInternet) {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.No_internet_connection)
                return@launch
            }
            if (alreadyAdded) return@launch
            alreadyAdded = true

            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value =
                networkImpl.addContact(
                    userId,
                    contact,
                    accessToken
                )
        }
    fun changeState() {
        _usersStateFlow.value = ApiStateUsers.Initial
    }
}