package com.rhorbachevskyi.viewpager.ui.fragments.addContacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
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

    private val _contactState = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val contactState: StateFlow<ApiStateUsers> get() = _contactState

    fun getAllUsers(accessToken: String, user: UserData) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        val apiService = ApiServiceFactory.createApiService()

        try {
            val response = UserRepository(apiService).getAllUsers("Bearer $accessToken")
            _usersStateFlow.value = response.data.let { ApiStateUsers.Success(it.users) }
            val filteredUsers = response.data.users?.filter { it.name != null && it.email !=  user.email}
            _users.postValue(filteredUsers?.map { it.toContact() } ?: emptyList())
        } catch (e: Exception) {
            _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    fun addContact(contactId: Long, accessToken: String, userId: Long) = viewModelScope.launch(Dispatchers.IO) {
        _contactState.value = ApiStateUsers.Loading
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).addContact(userId, "Bearer $accessToken", contactId)
            _contactState.value = response.data.let { ApiStateUsers.Success(it.users) }
        } catch (e: Exception) {
            _contactState.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }
}