package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(
    private val networkImpl: NetworkImpl,
    private val databaseImpl: DatabaseImpl
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
    fun getAllUsers(accessToken: String, user: UserData, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.Main) {
            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value = if (hasInternet) {
                networkImpl.getAllUsers(accessToken, user)
            } else {
                databaseImpl.getAllUsers()
            }
            _users.value = UserDataHolder.getServerList()
            startedListContact = _users.value
        }

    fun addContact(userId: Long, contact: Contact, accessToken: String, hasInternet: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            if (!supportList.contains(contact)) {
                supportList.add(contact)
                _states.value = arrayListOf(Pair(contact.id, ApiStateUsers.Loading))
                if(hasInternet) {
                    networkImpl.addContact(userId, contact, accessToken)
                } else {
                    _usersStateFlow.value = ApiStateUsers.Error(R.string.No_internet_connection)
                }
                _states.value = UserDataHolder.getStates()
            }
        }

    fun updateContactList(newText: String?): Int {
        val filteredList = startedListContact.filter { contact: Contact ->
            contact.name?.contains(newText ?: "", true) == true
        }
        _users.value = filteredList
        return filteredList.size
    }

    fun changeState() {
        _usersStateFlow.value = ApiStateUsers.Initial
    }
}