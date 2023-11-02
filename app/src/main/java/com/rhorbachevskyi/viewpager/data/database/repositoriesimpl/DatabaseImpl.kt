package com.rhorbachevskyi.viewpager.data.database.repositoriesimpl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.data.database.interfaces.ContactDao
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.ContactsPagingSource
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.UsersPageLoader
import com.rhorbachevskyi.viewpager.presentation.utils.Constants.PAGINATION_LIST_RANGE
import com.rhorbachevskyi.viewpager.presentation.utils.ext.fromEntity
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toContactEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val userDao: UserDao,
    private val searchDatabaseRepositoryImpl: SearchDatabaseRepositoryImpl,
    private val ioDispatcher: CoroutineDispatcher
) {
    private suspend fun getUsers(pageIndex: Int, pageSize: Int): List<Contact> =
        withContext(ioDispatcher) {
            val offset = pageIndex * pageSize

            val list = userDao.getUsers(pageSize, offset)
            return@withContext list.map { it.fromEntity() }
        }

    fun getPagedUsers(): Flow<PagingData<Contact>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_LIST_RANGE),
            pagingSourceFactory = { ContactsPagingSource(loader, PAGINATION_LIST_RANGE) }
        ).flow
    }

    fun getPagedContacts(): Flow<PagingData<Contact>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            getContacts(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(pageSize = PAGINATION_LIST_RANGE),
            pagingSourceFactory = { ContactsPagingSource(loader, PAGINATION_LIST_RANGE) }
        ).flow
    }

    private suspend fun getContacts(pageIndex: Int, pageSize: Int): List<Contact> =
        withContext(ioDispatcher) {
            val offset = pageIndex * pageSize
            val list = contactDao.getContacts(pageSize, offset)
            return@withContext list.map { it.fromEntity() }
        }

    suspend fun addUsers(users: List<Contact>) {
        userDao.deleteAllUsers()
        userDao.addUsers(users.map { it.toContactEntity() })
    }

    suspend fun addContacts(contacts: List<Contact>) {
        contactDao.deleteAllContacts()
        contactDao.addContacts(contacts.map { it.toContactEntity() })
    }

    suspend fun getSearchList(): List<Contact> =
        searchDatabaseRepositoryImpl.getList().map { user -> user.fromEntity() }


    suspend fun addToSearchList(contact: Contact) {
        searchDatabaseRepositoryImpl.addUser(contact.toContactEntity())
    }

    suspend fun deleteFromSearchList(contact: Contact) {
        searchDatabaseRepositoryImpl.deleteUser(contact.toContactEntity())
    }

    suspend fun addUsersToSearchList(users: List<Contact>) {
        searchDatabaseRepositoryImpl.addList(users.map { user -> user.toContactEntity() })
    }

}