package com.rhorbachevskyi.viewpager.domain.repository

import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.model.UserResponseContacts
import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.Body

class UserRepository constructor(private val service: ApiService) {
    suspend fun registerUser(@Body body: UserRequest): UserResponse {
        return service.registerUser(body)
    }

    suspend fun authorizeUser(@Body body: UserRequest): UserResponse {
        return service.authorizeUser(body)
    }

    suspend fun getAllUsers(accessToken: String): UsersResponse {
        return service.getAllUsers(accessToken)
    }

    suspend fun getUserContacts(userId: Long, accessToken: String): UserResponseContacts {
        return service.getUserContacts(userId, accessToken)
    }
    suspend fun addContact(userId: Long, accessToken: String, contactId: Long) : UsersResponse {
        return service.addContact(userId, accessToken, contactId)
    }
    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long) : UsersResponse {
        return service.deleteContact(userId, contactId, accessToken)
    }
}