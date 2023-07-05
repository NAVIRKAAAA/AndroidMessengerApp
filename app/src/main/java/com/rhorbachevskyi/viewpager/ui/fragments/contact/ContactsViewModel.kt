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
            log(response.data.toString())
        } catch (e: Exception) {
            log(e.toString())
            _usersStateFlow.value = ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    fun addContact(contact: Contact, position: Int = _contactList.value?.size ?: 0): Boolean {
        val contactList = _contactList.value?.toMutableList() ?: mutableListOf()

        if (!contactList.contains(contact)) {
            contactList.add(position, contact)
            _contactList.value = contactList
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

    fun deleteSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value?.toMutableList() ?: return false
        if (contactList.contains(contact)) {
            contactList.remove(contact)
            _selectContacts.value = contactList
            return true
        }

        return false
    }

    fun deleteContact(contact: Contact): Boolean {
        val contactList = _contactList.value?.toMutableList() ?: return false

        if (contactList.contains(contact)) {
            contactList.remove(contact)
            _contactList.value = contactList
            return true
        }

        return false
    }


    fun deleteSelectList() {
        val contactList = _selectContacts.value?.toMutableList() ?: return

        for (contact in contactList) {
            deleteSelectContact(contact)
            deleteContact(contact)
        }

        _selectContacts.value = contactList
    }

    fun changeMultiselectMode() {
        isMultiselect.value = !isMultiselect.value!!
        if (isMultiselect.value == true) _selectContacts.value = emptyList()
    }
}