package com.rhorbachevskyi.viewpager.ui.fragments.contact

import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.recyclerview.domain.localcontactdataset.LocalContactData
import com.rhorbachevskyi.viewpager.domain.model.Contact

class ContactsViewModel : ViewModel() {
    private val contacts = ArrayList<Contact>()
    private val selectContacts = ArrayList<Contact>()

    init {
        contacts.addAll(LocalContactData().getLocalContactsList())

    }

    fun getContactsList(): ArrayList<Contact> = contacts
    fun getSelectedContactsList(): ArrayList<Contact> = selectContacts
    fun deleteContact(contact: Contact): Boolean {
        return if (contacts.contains(contact)) {
            contacts.remove(contact)
            true
        } else false

    }

    fun addContact(contact: Contact, position: Int): Boolean {
        return if (!contacts.contains(contact)) {
            contacts.add(position, contact)
            true
        } else false
    }

    fun deleteSelectContact(contact: Contact): Boolean {
        return if (selectContacts.contains(contact)) {
            selectContacts.remove(contact)
            true
        } else false
    }

    fun addSelectContact(contact: Contact): Boolean {
        return if (!selectContacts.contains(contact)) {
            selectContacts.add(contact)
            true
        } else false
    }

    fun deleteSelectList() {
        for (i in selectContacts.size - 1 downTo 0) {
            deleteContact(selectContacts[i])
            deleteSelectContact(selectContacts[i])
        }
    }
}