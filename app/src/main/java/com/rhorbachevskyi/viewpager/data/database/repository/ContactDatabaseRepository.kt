package com.rhorbachevskyi.viewpager.data.database.repository

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.database.interfaces.ContactDao
import javax.inject.Inject

class ContactDatabaseRepository @Inject constructor(private val contactDao: ContactDao) :
    ContactDao {

    override suspend fun addContacts(contacts: List<ContactEntity>) {
        contactDao.addContacts(contacts)
    }

    override suspend fun updateContacts(contacts: List<ContactEntity>) {
        contactDao.updateContacts(contacts)
    }

    override suspend fun getContacts(): List<ContactEntity> = contactDao.getContacts()
    override suspend fun deleteAllContacts() {
        contactDao.deleteAllContacts()
    }
}