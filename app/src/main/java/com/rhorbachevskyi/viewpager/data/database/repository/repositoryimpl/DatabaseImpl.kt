package com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.SearchDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.utils.ext.fromEntity
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import javax.inject.Inject

class DatabaseImpl @Inject constructor(
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository,
    private val searchDatabaseRepository: SearchDatabaseRepository
) {
    suspend fun getAllUsers(): ApiState {
        return try {
            val response = userDatabaseRepository.getUsers()
            UserDataHolder.serverUsers = response.map { contactEntity -> contactEntity.fromEntity() }
            ApiState.Success<ArrayList<UserData>>(arrayListOf())
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun getAllContacts(): ApiState {
        return try {
            val response = contactDatabaseRepository.getContacts()
            UserDataHolder.serverContacts = response.map { contactEntity -> contactEntity.fromEntity() }
            ApiState.Success<ArrayList<UserData>>(arrayListOf())
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
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