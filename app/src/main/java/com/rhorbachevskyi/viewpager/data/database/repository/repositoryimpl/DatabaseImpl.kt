package com.rhorbachevskyi.viewpager.data.database.repository.repositoryimpl

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.utils.ext.fromEntity

import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class DatabaseImpl @Inject constructor(private val userDatabaseRepository: UserDatabaseRepository) {
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

    suspend fun addContact(contact: Contact): ApiStateUsers {
//        return try {
//            databaseRepository.addContact(contact.toEntity())
//            UserDataHolder.setStates(Pair(contact.id, ApiStateUsers.Success(arrayListOf())))
//            ApiStateUsers.Success(arrayListOf())
//        } catch (e: Exception) {
//            ApiStateUsers.Error(R.string.invalid_request)
//        }
        return ApiStateUsers.Loading
    }
}