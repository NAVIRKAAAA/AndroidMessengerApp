package com.rhorbachevskyi.recyclerview.ui.fragments.contact

import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.recyclerview.domain.localcontactdataset.LocalContactData
import com.rhorbachevskyi.recyclerview.domain.model.Contact

class ContactsViewModel : ViewModel() {
    private val contacts = ArrayList<Contact>()

    init {
        contacts.addAll(LocalContactData().getLocalContactsList())

    }
    fun getContactsList(): ArrayList<Contact> = contacts

    fun deleteContact(contact: Contact) : Boolean {
        if(contacts.contains(contact)) {
            contacts.remove(contact)
            return true
        }
        return false
    }
    fun addContact(contact: Contact, position: Int) : Boolean {
        if(!contacts.contains(contact)) {
            contacts.add(position, contact)
            return true
        }
        return false
    }
}