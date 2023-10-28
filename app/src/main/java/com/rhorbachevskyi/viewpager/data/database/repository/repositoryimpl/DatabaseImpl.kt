package com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl

import android.nfc.tech.MifareUltralight
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.interfaces.ContactDao
import com.rhorbachevskyi.viewpager.data.database.interfaces.UserDao
import com.rhorbachevskyi.viewpager.data.database.repository.SearchDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.paging.ContactsPagingSource
import com.rhorbachevskyi.viewpager.paging.UsersPageLoader
import com.rhorbachevskyi.viewpager.presentation.utils.ext.fromEntity
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DatabaseImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val userDao: UserDao,
    private val searchDatabaseRepository: SearchDatabaseRepository,
    private val ioDispatcher: CoroutineDispatcher
) {
    private suspend fun getUsers(pageIndex: Int, pageSize: Int): List<Contact> =
        withContext(ioDispatcher) {
            // calculate offset value required by DAO
            val offset = pageIndex * pageSize

            val list = userDao.getUsers(pageSize, offset)
            log("db: $list")
            // map UserDbEntity to User
            return@withContext list.map { it.fromEntity() }
        }

    fun getPagedUsers(): Flow<PagingData<Contact>> {
        val loader: UsersPageLoader = { pageIndex, pageSize ->
            getUsers(pageIndex, pageSize)
        }
        return Pager(
            config = PagingConfig(
                pageSize = MifareUltralight.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ContactsPagingSource(loader, MifareUltralight.PAGE_SIZE) }
        ).flow
    }

    suspend fun getAllContacts(): ApiState {
        return try {
            val response = contactDao.getContacts()
            UserDataHolder.serverContacts =
                response.map { contactEntity -> contactEntity.fromEntity() }
            ApiState.Success<ArrayList<UserData>>(arrayListOf())
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }
    suspend fun addUsers(users: List<Contact>) {
        userDao.deleteAllContacts()
        userDao.addUsers(users.map { it.toEntity() })
    }
    suspend fun addContacts(contacts: List<Contact>) {
        contactDao.deleteAllContacts()
        contactDao.addContacts(contacts.map { it.toEntity() })
    }
    suspend fun getSearchList(): List<Contact> =
        searchDatabaseRepository.getList().map { user -> user.fromEntity() }


    suspend fun addToSearchList(contact: Contact) {
        searchDatabaseRepository.addUser(contact.toEntity())
    }

    suspend fun deleteFromSearchList(contact: Contact) {
        searchDatabaseRepository.deleteUser(contact.toEntity())
    }

    suspend fun addUsersToSearchList(users: List<Contact>) {
        searchDatabaseRepository.addList(users.map { user -> user.toEntity() })
    }

}