package com.rhorbachevskyi.viewpager.domain.di.network

import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("users")
    suspend fun registerUser(@Body body: UserRequest): UserResponse


    @POST("login")
    suspend fun authorizeUser(@Body body: UserRequest): UserResponse

    @GET("users")
    suspend fun getAllUsers(@Header("Authorization") accessToken: String): UserResponse

}