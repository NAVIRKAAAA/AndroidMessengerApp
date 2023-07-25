package com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.SearchDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.utils.ext.fromEntity
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DatabaseImpl @Inject constructor(
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository,
    private val searchDatabaseRepository: SearchDatabaseRepository
) {
    suspend fun getAllUsers(): ApiStateUsers {
        return try {
            val response = userDatabaseRepository.getUsers()
            val users: MutableStateFlow<List<Contact>> = MutableStateFlow(emptyList())
            users.value =
                response.map { contactEntity -> contactEntity.fromEntity() }
            UserDataHolder.setServerList(users)
            ApiStateUsers.Success(arrayListOf())
        } catch (e: Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getAllContacts(): ApiStateUsers {
        return try {
            val response = contactDatabaseRepository.getContacts()
            val users: MutableStateFlow<List<Contact>> = MutableStateFlow(emptyList())
            users.value =
                response.map { contactEntity -> contactEntity.fromEntity() }
            UserDataHolder.setContactList(users)
            ApiStateUsers.Success(arrayListOf())
        } catch (e: Exception) {
            ApiStateUsers.Error(R.string.invalid_request)
        }
    }

    suspend fun getSearchList(): List<Contact> {
        return try {
            searchDatabaseRepository.getList().map { user -> user.fromEntity() }
        } catch (e: Exception) {
            listOf()
        }
    }

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