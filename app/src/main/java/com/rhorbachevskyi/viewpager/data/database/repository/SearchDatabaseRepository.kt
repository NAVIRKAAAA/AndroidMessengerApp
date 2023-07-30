package com.rhorbachevskyi.viewpager.data.database.repository

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.SearchDao
import javax.inject.Inject

class SearchDatabaseRepository @Inject constructor(private val searchDao: SearchDao) : SearchDao {
    override suspend fun addList(contacts: List<ContactEntity>) {
        deleteUsers()
        searchDao.addList(contacts)
    }

    override suspend fun deleteUsers() {
        searchDao.deleteUsers()
    }

    override suspend fun addUser(contact: ContactEntity) {
        searchDao.addUser(contact)
    }

    override suspend fun deleteUser(contact: ContactEntity) {
        searchDao.deleteUser(contact)
    }

    override suspend fun getList(): List<ContactEntity> = searchDao.getList()
}