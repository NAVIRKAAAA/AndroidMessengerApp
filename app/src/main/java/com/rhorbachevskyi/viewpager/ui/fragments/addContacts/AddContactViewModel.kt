package com.rhorbachevskyi.viewpager.ui.fragments.addContacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.domain.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.utils.ApiServiceFactory
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddContactViewModel : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _users = MutableLiveData<List<Contact>>()
    val users: LiveData<List<Contact>> get() = _users

    private var _states: MutableStateFlow<Array<Pair<Long, ApiStateUsers>>> = MutableStateFlow(emptyArray())
    val states: StateFlow<Array<Pair<Long, ApiStateUsers>>> = _states

    private var contactList = ArrayList<Contact>()

    fun getAllUsers(accessToken: String, user: UserData) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        val apiService = ApiServiceFactory.createApiService()
        getContactList(apiService, user.id, accessToken)
        try {
            val response = UserRepository(apiService).getAllUsers("Bearer $accessToken")
            _usersStateFlow.value = response.data.let { ApiStateUsers.Success(it.users) }

            val filteredUsers =
                response.data.users?.filter { it.name != null && it.email != user.email && !contactList.contains(it.toContact()) }
            _users.postValue(filteredUsers?.map { it.toContact() } ?: emptyList())
        } catch (e: Exception) {
            _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    private fun getContactList(apiService: ApiService, userId: Long, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            contactList = try {
                val response =
                    UserRepository(apiService).getUserContacts(userId, "Bearer $accessToken")
                (response.data.contacts?.map { it.toContact() } ?: emptyList()) as ArrayList<Contact>
            } catch (e: Exception) {
                ArrayList()
            }
        }

    fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _states.value = arrayOf(Pair(contact.id, ApiStateUsers.Loading))
            val apiService = ApiServiceFactory.createApiService()
            try {
                val response =
                    UserRepository(apiService).addContact(userId, "Bearer $accessToken", contact.id)
                _states.value = arrayOf(Pair(contact.id, ApiStateUsers.Success(response.data.users)))
                contact.isAdd = true
            } catch (e: Exception) {
                _states.value = arrayOf(Pair(contact.id, ApiStateUsers.Error(R.string.invalid_request)))
            }
        }
}