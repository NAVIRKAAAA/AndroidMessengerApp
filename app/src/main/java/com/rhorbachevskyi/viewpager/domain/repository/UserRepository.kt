package com.rhorbachevskyi.viewpager.domain.repository

import com.rhorbachevskyi.viewpager.domain.di.network.ApiService
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import retrofit2.http.Body

class UserRepository constructor(private val service: ApiService) {
    suspend fun registerUser(@Body body: UserRequest): UserResponse {
        return service.registerUser(body)
    }
    suspend fun authorizeUser(@Body body: UserRequest): UserResponse {
        return service.authorizeUser(body)
    }
    suspend fun getAllUsers(accessToken: String): UserResponse {
        return service.getAllUsers(accessToken)
    }
}