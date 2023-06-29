package com.rhorbachevskyi.viewpager.domain.di.network

import com.rhorbachevskyi.viewpager.domain.model.EditUser
import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users")
    suspend fun registerUser(@Body body: UserRequest): UserResponse
    @PUT("users/{userId}")
    suspend fun editUser(
        @Path("userId") userId: String,
        @Header("Authorization") accessToken: String,
        @Body body: EditUser
    ): UserResponse
}