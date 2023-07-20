package com.rhorbachevskyi.viewpager.data.database.repository

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.ContactDao
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import javax.inject.Inject

class ContactDatabaseRepository @Inject constructor(private val contactDao: ContactDao) : ContactDao {
    override suspend fun addContact(contact: ContactEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteContact(contact: ContactEntity) {
        TODO("Not yet implemented")
    }
}