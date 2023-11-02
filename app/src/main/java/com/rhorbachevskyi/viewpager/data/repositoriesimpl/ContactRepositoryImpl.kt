package com.rhorbachevskyi.viewpager.data.repositoriesimpl

import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.database.repositoriesimpl.DatabaseImpl
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactService: ContactApiService,
    private val databaseImpl: DatabaseImpl
) {
    suspend fun getUsers(
        accessToken: String,
        user: UserData,
    ): ApiState {
        return try {
            val response = contactService.getUsers("${Constants.AUTH_PREFIX} $accessToken")
            if (response.data.users == null) return ApiState.Error(0)

            val serverContacts = UserDataHolder.serverContacts

            val filteredUsers = response.data.users.filter {
                it.name != null && it.email != user.email && !serverContacts.contains(it.toContact())
            }
            // holder, database
            UserDataHolder.serverUsers = filteredUsers.map { it.toContact() }

            databaseImpl.addUsers(UserDataHolder.serverUsers)
            // result
            filteredUsers.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun getContacts(
        accessToken: String,
        userId: Long
    ): ApiState {
        return try {
            val response =
                contactService.getUserContacts("${Constants.AUTH_PREFIX} $accessToken", userId)
            if (response.data.contacts == null) return ApiState.Error(0)

            val contacts = response.data.contacts.map { it.toContact() }

            // holder, database
            UserDataHolder.serverContacts = contacts
            databaseImpl.addContacts(contacts)

            contacts.let { ApiState.Success(it) }
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun addContact(accessToken: String,userId: Long, contactId: Long): ApiState {
        return try {
            val response =
                contactService.addContact(
                    "${Constants.AUTH_PREFIX} $accessToken",
                    userId,
                    contactId
                )
            UserDataHolder.states.add(contactId to ApiState.Success(response.data.users))
            response.data.let { ApiState.Success(it.users) }
        } catch (e: Exception) {
            UserDataHolder.states.add(contactId to ApiState.Error(R.string.invalid_request))
            ApiState.Error(R.string.invalid_request)
        }
    }

    suspend fun deleteContact(accessToken: String, userId: Long, contactId: Long): ApiState {
        return try {
            val response = contactService.deleteContact(
                "${Constants.AUTH_PREFIX} $accessToken",
                userId,
                contactId
            )
            response.data.let { ApiState.Success(it.users) }
        } catch (e: java.lang.Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }
}