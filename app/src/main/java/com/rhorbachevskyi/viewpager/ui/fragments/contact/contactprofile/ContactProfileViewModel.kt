package com.rhorbachevskyi.viewpager.ui.fragments.contact.contactprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactProfileViewModel : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private var alreadyAdded = false

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if(!alreadyAdded) {
                alreadyAdded = true
                _usersStateFlow.value = ApiStateUsers.Loading
                NetworkImplementation.addContact(userId, contact, accessToken)
                _usersStateFlow.value = NetworkImplementation.getStateUserAction()
            }
        }
}