package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repository.repositoryimpl.NetworkImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val networkImpl: NetworkImpl
) : ViewModel() {

    private val _usersStateFlow = MutableStateFlow<ApiStateUsers>(ApiStateUsers.Initial)
    val usersState: StateFlow<ApiStateUsers> = _usersStateFlow

    private val _contactList = MutableStateFlow(listOf<Contact>())
    val contactList: StateFlow<List<Contact>> = _contactList

    private val _selectContacts = MutableLiveData<List<Contact>>(listOf())
    val selectContacts: LiveData<List<Contact>> = _selectContacts

    val isMultiselect = MutableLiveData(false)

    private val _isSelectItem: MutableStateFlow<ArrayList<Pair<Boolean, Int>>> =
        MutableStateFlow(ArrayList())
    val isSelectItem: StateFlow<ArrayList<Pair<Boolean, Int>>> = _isSelectItem
    // search helper
    private val startedListContact: ArrayList<Contact> = arrayListOf()

    fun initialContactList(userId: Long, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value = networkImpl.getContacts(userId, accessToken)
            _contactList.value = UserDataHolder.getContacts()
            startedListContact.clear()
            startedListContact.addAll(_contactList.value)
        }

    private fun addContact(userId: Long, contact: Contact, accessToken: String) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value = ApiStateUsers.Loading
            _usersStateFlow.value =
                networkImpl.addContact(userId, contact, accessToken)
        }

    fun addContactToList(
        userId: Long,
        contact: Contact,
        accessToken: String,
        position: Int = _contactList.value.size
    ): Boolean {
        val contactList = _contactList.value.toMutableList()

        if (!contactList.contains(contact)) {
            contactList.add(position, contact)
            _contactList.value = contactList
            addContact(userId, contact, accessToken)
            startedListContact.add(contact)
            return true
        }

        return false
    }

    fun addSelectContact(contact: Contact): Boolean {
        val contactList = _selectContacts.value?.toMutableList() ?: mutableListOf()

        if (!contactList.contains(contact)) {
            contactList.add(contact)
            _selectContacts.value = contactList
            _isSelectItem.value.add(Pair(true, contact.id.toInt()))
            return true
        }

        return false
    }

    private fun deleteContact(userId: Long, accessToken: String, contactId: Long) =
        viewModelScope.launch(Dispatchers.IO) {
            _usersStateFlow.value =
                networkImpl.deleteContact(userId, accessToken, contactId)
        }

    fun deleteContactFromList(userId: Long, accessToken: String, contactId: Long): Boolean {
        val contact = _contactList.value.find { it.id == contactId }
        val contactList = _contactList.value.toMutableList()

        if (contactList.contains(contact)) {
            deleteContact(userId, accessToken, contactId)
            contactList.remove(contact)
            _contactList.value = contactList
            startedListContact.remove(contact)
            return true
        }
        _usersStateFlow.value = ApiStateUsers.Error(R.string.contact_not_found)
        return false
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
            val id = contact.id.toInt()
            val index = _isSelectItem.value.indexOfFirst { it.second == id }
            _isSelectItem.value.removeAt(index)
            return true
        }

        return false
    }

    fun changeMultiselectMode() {
        isMultiselect.value = !isMultiselect.value!!
        if (isMultiselect.value == false) {
            _selectContacts.value = emptyList()
            _isSelectItem.value.clear()
        }
    }

    fun updateContactList(newText: String?): Int {
        val filteredList = startedListContact.filter { contact: Contact ->
            contact.name?.contains(newText ?: "", ignoreCase = true) == true
        }
        _contactList.value = filteredList
        return filteredList.size
    }

    fun deleteStates() {
        UserDataHolder.deleteStates()
    }
}