package com.rhorbachevskyi.viewpager.ui.fragments.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.domain.repository.UserRepository
import com.rhorbachevskyi.viewpager.domain.utils.ApiServiceFactory
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.utils.ext.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class ContactsViewModel : ViewModel() {
    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _contactList = MutableLiveData(listOf<Contact>())
    val contactList: LiveData<List<Contact>> get() = _contactList

    private val _selectContacts = MutableLiveData<List<Contact>>(listOf())
    val selectContacts: LiveData<List<Contact>> get() = _selectContacts

    val isMultiselect = MutableLiveData(false)


    fun initList(userId: Long, accessToken: String) = viewModelScope.launch(Dispatchers.IO) {
        _usersStateFlow.value = ApiStateUsers.Loading
        val apiService = ApiServiceFactory.createApiService()
        try {
            val response = UserRepository(apiService).getUserContacts(userId, "Bearer $accessToken")
            val users = response.data.contacts
            _usersStateFlow.value = users.let {
                ApiStateUsers.Success(it)
            }
            if (!users.isNullOrEmpty()) {
                _contactList.postValue(users.map { it.toContact() })
            }
        } catch (e: Exception) {
            _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    private fun addContact(userId: Long, contactId: Long, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value = ApiStateUsers.Loading
            val apiService = ApiServiceFactory.createApiService()
            try {
                val response =
                    UserRepository(apiService).addContact(userId, "Bearer $accessToken", contactId)
                _usersStateFlow.value = response.data.let { ApiStateUsers.Success(it.users) }
            } catch (e: Exception) {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
            }
        }

    fun addContactToList(userId: Long, contact: Contact, accessToken: String, position: Int = _contactList.value?.size ?: 0): Boolean {
        val contactList = _contactList.value?.toMutableList() ?: mutableListOf()

        if (!contactList.contains(contact)) {
            contactList.add(position, contact)
            _contactList.value = contactList
            addContact(userId, contact.id, accessToken)
            return true
        }

        return false
    }

    fun addSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value?.toMutableList() ?: mutableListOf()

        if (!contactList.contains(contact)) {
            contactList.add(contact)
            _selectContacts.value = contactList
            return true
        }

        return false
    }

    fun deleteContactFromList(userId: Long, accessToken: String, contactId: Long): Boolean {
        val contact = _contactList.value?.find { it.id == contactId }
        val contactList = _contactList.value?.toMutableList() ?: return false

        if (contactList.contains(contact)) {
            contactList.remove(contact)
            _contactList.value = contactList
            deleteContact(userId, accessToken, contactId)
            return true
        }

        return false
    }


    private fun deleteContact(userId: Long, accessToken: String, contactId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            val apiService = ApiServiceFactory.createApiService()
            try {
                val response = UserRepository(apiService).deleteContact(
                    userId,
                    "Bearer $accessToken",
                    contactId
                )
                log(response.data.toString())
            } catch (e: Exception) {
                _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
            }
        }

    fun deleteSelectList(userId: Long, accessToken: String) {
        val contactList = _selectContacts.value?.toMutableList() ?: return

        for (contact in contactList) {
            deleteContactFromList(userId, accessToken, contact.id)
            deleteSelectContact(contact)
        }

        _selectContacts.value = contactList
    }
    fun deleteSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value?.toMutableList() ?: return false
        if (contactList.contains(contact)) {
            contactList.remove(contact)
            _selectContacts.value = contactList
            return true
        }

        return false
    }
    fun changeMultiselectMode() {
        isMultiselect.value = !isMultiselect.value!!
        if (isMultiselect.value == true) _selectContacts.value = emptyList()
    }
}