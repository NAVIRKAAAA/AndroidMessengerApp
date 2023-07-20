package com.rhorbachevskyi.viewpager.data.database.repository

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val userDao: UserDao) : UserDao {
    override suspend fun updateUsers(users: List<ContactEntity>) {
        userDao.updateUsers(users)
    }

    override suspend fun getUsers(): List<ContactEntity> = userDao.getUsers()
    override suspend fun addUsers(users: List<ContactEntity>) {
        userDao.addUsers(users)
    }

}