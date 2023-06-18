package com.rhorbachevskyi.recyclerview.ui.fragments.contact

import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.recyclerview.domain.localuserdataset.LocalContactData
import com.rhorbachevskyi.recyclerview.domain.model.Contact

class ContactsViewModel : ViewModel() {
    private val users = ArrayList<Contact>()

    init {
        users.addAll(LocalContactData().getLocalContactsList())

    }
    fun getUserList(): ArrayList<Contact> = users

    fun deleteUser(user: Contact) : Boolean {
        if(users.contains(user)) {
            users.remove(user)
            return true
        }
        return false
    }
    fun addUser(user: Contact, position: Int) : Boolean {
        if(!users.contains(user)) {
            users.add(position, user)
            return true
        }
        return false
    }
}