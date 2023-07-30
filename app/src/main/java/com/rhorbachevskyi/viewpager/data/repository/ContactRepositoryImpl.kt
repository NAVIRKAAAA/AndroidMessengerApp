package com.rhorbachevskyi.viewpager.data.repository

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repository.ContactDatabaseRepository
import com.rhorbachevskyi.viewpager.data.database.repository.UserDatabaseRepository
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.toEntity
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactService: ContactApiService,
    private val userDatabaseRepository: UserDatabaseRepository,
    private val contactDatabaseRepository: ContactDatabaseRepository,
) {
    suspend fun getAllUsers(accessToken: String, user: UserData): ApiStateUser {
        return try {
            val response =
                contactService.getAllUsers("${Constants.AUTHORIZATION_PREFIX} $accessToken")
            val contacts = UserDataHolder.serverContacts
            val filteredUsers =
                response.data.users?.filter {
                    it.name != null && it.email != user.email && !contacts.contains(it.toContact())
                }
            val users = filteredUsers?.map { it.toContact() } ?: emptyList()
            UserDataHolder.serverUsers = users
            userDatabaseRepository.addUsers(users.map { contact -> contact.toEntity() })
            response.data.let { ApiStateUser.Success(it.users) }
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(userId: Long, accessToken: String): ApiStateUser {
        return try {
            val response =
                contactService.getUserContacts(
                    userId,
                    "${Constants.AUTHORIZATION_PREFIX} $accessToken"
                )
            val users = response.data.contacts?.map { it.toContact() } ?: emptyList()
            UserDataHolder.serverContacts = users
            contactDatabaseRepository.deleteAllContacts()
            contactDatabaseRepository.addContacts(users.map { contact -> contact.toEntity() })
            response.data.let { ApiStateUser.Success(it.contacts) }
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }

    suspend fun addContact(userId: Long, contact: Contact, accessToken: String): ApiStateUser {
        return try {
            val response =
                contactService.addContact(
                    userId,
                    "${Constants.AUTHORIZATION_PREFIX} $accessToken",
                    contact.id
                )
            UserDataHolder.states.add(contact.id to ApiStateUser.Success(response.data.users))
            response.data.let { ApiStateUser.Success(it.users) }
        } catch (e: Exception) {
            UserDataHolder.states.add(
                contact.id to ApiStateUser.Error(R.string.invalid_request)
            )
            ApiStateUser.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(userId: Long, accessToken: String, contact: Contact): ApiStateUser {
        return try {
            val response = contactService.deleteContact(
                userId,
                contact.id,
                "${Constants.AUTHORIZATION_PREFIX} $accessToken",
            )
            response.data.let { ApiStateUser.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }
}