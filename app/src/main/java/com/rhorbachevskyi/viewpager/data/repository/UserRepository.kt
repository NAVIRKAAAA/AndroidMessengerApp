package com.rhorbachevskyi.viewpager.data.repository

import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import retrofit2.http.Body
import java.util.Date
import javax.inject.Inject

class UserRepository @Inject constructor(private val service: UserApiService) {
    suspend fun registerUser(@Body body: UserRequest): UserResponse = service.registerUser(body)
    suspend fun authorizeUser(@Body body: UserRequest): UserResponse =service.authorizeUser(body)
    suspend fun getUser(userId: Long, accessToken: String) : UserResponse = service.getUser(userId, accessToken)
    suspend fun editUser(
        id: Long, accessToken: String,
        name: String, career: String?, phone: String, address: String?, date: Date?
    ): UserResponse = service.editUser(id, accessToken, name, career, phone, address, date)
}