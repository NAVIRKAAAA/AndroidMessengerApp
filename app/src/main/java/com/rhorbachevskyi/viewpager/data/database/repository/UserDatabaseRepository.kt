package com.rhorbachevskyi.viewpager.data.database.repository

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import javax.inject.Inject

class UserDatabaseRepository @Inject constructor(private val userDao: UserDao) : UserDao {

    override suspend fun addUsers(users: List<ContactEntity>) {
        deleteAllContacts()
        userDao.addUsers(users)
    }

    override suspend fun updateUsers(users: List<ContactEntity>) {
        userDao.updateUsers(users)
    }

    override suspend fun getUsers(): List<ContactEntity> = userDao.getUsers()
    override suspend fun deleteAllContacts() {
        userDao.deleteAllContacts()
    }

}