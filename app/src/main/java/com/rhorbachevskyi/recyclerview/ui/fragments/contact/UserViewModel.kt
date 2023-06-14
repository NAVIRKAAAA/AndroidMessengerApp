package com.rhorbachevskyi.recyclerview.ui.fragments.contact

import androidx.lifecycle.ViewModel

import com.rhorbachevskyi.recyclerview.domain.localuserdataset.LocalUserData
import com.rhorbachevskyi.recyclerview.domain.model.User

class UserViewModel : ViewModel() {
    private val users = ArrayList<User>()

    init {
        users.addAll(LocalUserData().getLocalContactsList())

    }
    fun getUserList(): ArrayList<User> = users

    fun deleteUser(user: User) : Boolean {
        if(users.contains(user)) {
            users.remove(user)
            return true
        }
        return false
    }
    fun addUser(user: User, position: Int) : Boolean {
        if(!users.contains(user)) {
            users.add(position, user)
            return true
        }
        return false
    }
}