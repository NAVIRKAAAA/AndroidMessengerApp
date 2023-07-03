package com.rhorbachevskyi.viewpager.ui.fragments.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.viewpager.data.localcontactdataset.LocalContactData
import com.rhorbachevskyi.viewpager.data.model.Contact

class ContactsViewModel : ViewModel() {
    private val _contactList = MutableLiveData(listOf<Contact>())
    val contactList: LiveData<List<Contact>> get() = _contactList

    private val _selectContacts = MutableLiveData<List<Contact>>(listOf())
    val selectContacts: LiveData<List<Contact>> get() = _selectContacts

    val isMultiselect = MutableLiveData(false)

    init {
        _contactList.value = LocalContactData().getLocalContactsList().toMutableList()
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