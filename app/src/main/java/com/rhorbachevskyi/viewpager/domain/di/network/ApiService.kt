package com.rhorbachevskyi.viewpager.domain.di.network

import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.model.UserResponseContacts
import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("users")
    suspend fun registerUser(@Body body: UserRequest): UserResponse


    @POST("login")
    suspend fun authorizeUser(@Body body: UserRequest): UserResponse

    @GET("users")
    suspend fun getAllUsers(@Header("Authorization") accessToken: String): UsersResponse

    @GET("users/{userId}/contacts")
    suspend fun getUserContacts(
        @Path("userId") userId: Long,
        @Header("Authorization") accessToken: String
    ): UserResponseContacts
    @FormUrlEncoded
    @PUT("users/{userId}/contacts")
    suspend fun addContact(
        @Path("userId") userId: Long, @Header("Authorization") tokenHeader: String,
        @Field("contactId") contactId: Long
    ): UsersResponse

}