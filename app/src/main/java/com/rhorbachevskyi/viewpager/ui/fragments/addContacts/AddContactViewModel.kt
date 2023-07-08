package com.rhorbachevskyi.viewpager.ui.fragments.addContacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactViewModel : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _users = MutableLiveData<List<Contact>>()
    val users: LiveData<List<Contact>> get() = _users

    private val _states: MutableStateFlow<ArrayList<Pair<Long, ApiStateUsers>>> =
        MutableStateFlow(ArrayList())
    val states: StateFlow<ArrayList<Pair<Long, ApiStateUsers>>> = _states
    val supportList: ArrayList<Contact> = arrayListOf()

    fun getAllUsers(accessToken: String, user: UserData) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        NetworkImplementation.getAllUsers(accessToken, user)
        withContext(Dispatchers.Main) {
            _usersStateFlow.value = NetworkImplementation.getStateUserAction()
            _users.value = NetworkImplementation.getServerUsers()
        }
    }

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if(!supportList.contains(contact)) {
                supportList.add(contact)
                _states.value = arrayListOf(Pair(contact.id, ApiStateUsers.Loading))
                NetworkImplementation.addContact(userId, contact, accessToken)
                _states.value = NetworkImplementation.getStateAddContact()
            }  else {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.already_have_this_a_contact)
            }
        }
}