package com.rhorbachevskyi.viewpager.data.userdataholder

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import kotlinx.coroutines.flow.MutableStateFlow

object UserDataHolder {
    private var states: ArrayList<Pair<Long, ApiStateUsers>> = ArrayList()
    private val serverUsers = MutableStateFlow<List<Contact>>(listOf())
    private var contacts = ArrayList<Contact>()

    fun setServerList(serverUsers: MutableStateFlow<List<Contact>>) {
        UserDataHolder.serverUsers.value = serverUsers.value
    }

    fun setContactList(contacts: ArrayList<Contact>) {
        UserDataHolder.contacts = contacts
    }

    fun setStates(state : Pair<Long, ApiStateUsers>) {
        states.add(state)
    }

    fun getServerList(): List<Contact> = serverUsers.value
    fun getContacts(): ArrayList<Contact> = contacts
    fun getStates() : ArrayList<Pair<Long, ApiStateUsers>> = states
    fun deleteStates() {
        states.clear()
    }
}