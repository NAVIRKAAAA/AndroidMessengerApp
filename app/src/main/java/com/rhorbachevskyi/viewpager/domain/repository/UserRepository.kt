package com.rhorbachevskyi.viewpager.domain.repository

import com.rhorbachevskyi.viewpager.di.NetworkService
import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.model.UserResponse
import retrofit2.http.Body

class UserRepository constructor(private val networkService: NetworkService) {
    suspend fun registerUser(@Body body: UserRequest): UserResponse {
        return networkService.registerUser(body)
    }
}