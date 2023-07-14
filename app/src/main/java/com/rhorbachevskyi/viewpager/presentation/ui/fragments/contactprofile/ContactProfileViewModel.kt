package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import com.rhorbachevskyi.viewpager.data.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if(alreadyAdded) return@launch
            alreadyAdded = true

            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value = NetworkImplementation(userRepository).addContact(userId, contact, accessToken)

        }
}