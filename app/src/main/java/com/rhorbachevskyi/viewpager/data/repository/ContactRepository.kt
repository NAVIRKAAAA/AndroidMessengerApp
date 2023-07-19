package com.rhorbachevskyi.viewpager.data.repository

import com.rhorbachevskyi.viewpager.data.model.UserResponseContacts
import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import javax.inject.Inject

class ContactRepository @Inject constructor(private val service: ContactApiService) {
    suspend fun getAllUsers(accessToken: String): UsersResponse =
        service.getAllUsers(accessToken)

    suspend fun getUserContacts(userId: Long, accessToken: String): UserResponseContacts =
        service.getUserContacts(userId, accessToken)

    suspend fun addContact(userId: Long, accessToken: String, contactId: Long): UsersResponse =
        service.addContact(userId, accessToken, contactId)

    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long): UsersResponse =
        service.deleteContact(userId, contactId, accessToken)
}