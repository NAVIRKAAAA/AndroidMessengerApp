package com.rhorbachevskyi.viewpager.domain.repository

import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.model.UserResponseContacts
import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.Body

class UserRepository constructor(private val service: ApiService) {
    suspend fun registerUser(@Body body: UserRequest): UserResponse = service.registerUser(body)

    suspend fun authorizeUser(@Body body: UserRequest): UserResponse =service.authorizeUser(body)

    suspend fun getAllUsers(accessToken: String): UsersResponse =service.getAllUsers(accessToken)

    suspend fun getUserContacts(userId: Long, accessToken: String): UserResponseContacts =service.getUserContacts(userId, accessToken)
    suspend fun addContact(userId: Long, accessToken: String, contactId: Long) : UsersResponse =service.addContact(userId, accessToken, contactId)
    suspend fun deleteContact(userId: Long, accessToken: String, contactId: Long) : UsersResponse = service.deleteContact(userId, contactId, accessToken)
    suspend fun getUser(userId: Long, accessToken: String) : UserResponse = service.getUser(userId, accessToken)
    suspend fun editUser(
        id: Long, accessToken: String,
        name: String, career: String?, phone: String, address: String?, date: String?
    ): UserResponse = service.editUser(id, accessToken, name, career, phone, address, date)
}