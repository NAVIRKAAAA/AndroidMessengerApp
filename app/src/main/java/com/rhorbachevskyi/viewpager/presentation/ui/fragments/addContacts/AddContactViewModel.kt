package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.repository.NetworkImplementation
import com.rhorbachevskyi.viewpager.data.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _users = MutableLiveData<List<Contact>>()
    val users: LiveData<List<Contact>> get() = _users

    private val _states: MutableStateFlow<ArrayList<Pair<Long, ApiStateUsers>>> =
        MutableStateFlow(ArrayList())
    val states: StateFlow<ArrayList<Pair<Long, ApiStateUsers>>> = _states
    val supportList: ArrayList<Contact> = arrayListOf()
    private var startedListContact: List<Contact> = listOf()
    fun getAllUsers(accessToken: String, user: UserData) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        _usersStateFlow.value = NetworkImplementation(userRepository).getAllUsers(accessToken, user)
        withContext(Dispatchers.Main) {
            _users.value = NetworkImplementation(userRepository).getServerUsers()
            startedListContact = NetworkImplementation(userRepository).getServerUsers()!!
        }
    }

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            if(!supportList.contains(contact)) {
                supportList.add(contact)
                _states.value = arrayListOf(Pair(contact.id, ApiStateUsers.Loading))
                NetworkImplementation(userRepository).addContact(userId, contact, accessToken)
                _states.value = NetworkImplementation(userRepository).getStateAddContact()
            }  else {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.already_have_this_a_contact)
            }
        }

    fun updateContactList(newText: String?) : Int {
        val filteredList = startedListContact.filter { contact: Contact ->
            contact.name?.contains(newText ?: "", true) == true
        }
        _users.value = filteredList
        return filteredList.size
    }
}