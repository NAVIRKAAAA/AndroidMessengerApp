package com.rhorbachevskyi.viewpager.di

import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkService {
    @POST("users")
    suspend fun registerUser(@Body body: UserRequest): UserResponse
}