package com.rhorbachevskyi.viewpager.ui.fragments.contact

import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.viewpager.domain.localcontactdataset.LocalContactData
import com.rhorbachevskyi.viewpager.domain.model.Contact
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {
    private val contacts = ArrayList<Contact>()
    private val selectContacts = ArrayList<Contact>()

    init {
        contacts.addAll(LocalContactData().getLocalContactsList())
    }

    fun getContactsList(): ArrayList<Contact> = contacts
    fun getSelectedContactsList(): ArrayList<Contact> = selectContacts
    fun deleteContact(contact: Contact): Boolean {
        if (contacts.contains(contact)) {
            contacts.remove(contact)
            return true
        }
        return false
    }

    fun addContact(contact: Contact, position: Int): Boolean {
        if (!contacts.contains(contact)) {
            contacts.add(position, contact)
            return true
        }
        return false
    }

    fun deleteSelectContact(contact: Contact): Boolean {
        if (selectContacts.contains(contact)) {
            selectContacts.remove(contact)
            return true
        }
        return false
    }

    fun addSelectContact(contact: Contact): Boolean {
        if (!selectContacts.contains(contact)) {
            selectContacts.add(contact)
            return true
        }
        return false
    }

    fun deleteSelectList() {
        for (i in selectContacts.size - 1 downTo 0) {
            deleteContact(selectContacts[i])
            deleteSelectContact(selectContacts[i])
        }
    }
}