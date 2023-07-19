package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.UserDataHolder
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val networkImpl: NetworkImpl
) : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _users = MutableStateFlow<List<Contact>>(listOf())
    val users: StateFlow<List<Contact>> = _users

    private val _states: MutableStateFlow<ArrayList<Pair<Long, ApiStateUsers>>> =
        MutableStateFlow(ArrayList())
    val states: StateFlow<ArrayList<Pair<Long, ApiStateUsers>>> = _states
    // so that it does not always depend on the server
    val supportList: ArrayList<Contact> = arrayListOf()
    // search helper
    private var startedListContact: List<Contact> = listOf()
    fun getAllUsers(accessToken: String, user: UserData) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        _usersStateFlow.value = networkImpl.getAllUsers(accessToken, user)
        withContext(Dispatchers.Main) {
            _users.value = UserDataHolder.getServerList()
            startedListContact = _users.value
        }
    }

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!supportList.contains(contact)) {
                supportList.add(contact)
                _states.value = arrayListOf(Pair(contact.id, ApiStateUsers.Loading))
                networkImpl.addContact(userId, contact, accessToken)
                _states.value = UserDataHolder.getStates()
            } else {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.already_have_this_a_contact)
            }
        }

    fun updateContactList(newText: String?): Int {
        val filteredList = startedListContact.filter { contact: Contact ->
            contact.name?.contains(newText ?: "", true) == true
        }
        _users.value = filteredList
        return filteredList.size
    }
}