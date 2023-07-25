package com.rhorbachevskyi.viewpager.data.userdataholder

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import kotlinx.coroutines.flow.MutableStateFlow

object UserDataHolder {
    private var states: ArrayList<Pair<Long, ApiStateUsers>> = ArrayList()
    private val serverUsers = MutableStateFlow<List<Contact>>(listOf())
    private val serverContacts = MutableStateFlow<List<Contact>>(listOf())
    private lateinit var userData: UserResponse.Data

    fun setServerList(serverUsers: MutableStateFlow<List<Contact>>) {
        this.serverUsers.value = serverUsers.value
    }

    fun setContactList(contacts: MutableStateFlow<List<Contact>>) {
        this.serverContacts.value = contacts.value
    }

    fun setStates(state: Pair<Long, ApiStateUsers>) {
        states.add(state)
    }

    fun setUser(userData: UserResponse.Data) {
        this.userData = userData
    }


    fun getServerList(): List<Contact> = serverUsers.value
    fun getContacts(): List<Contact> = serverContacts.value
    fun getStates(): ArrayList<Pair<Long, ApiStateUsers>> = states
    fun getUserData(): UserResponse.Data = userData
    fun deleteStates() {
        states.clear()
    }
}