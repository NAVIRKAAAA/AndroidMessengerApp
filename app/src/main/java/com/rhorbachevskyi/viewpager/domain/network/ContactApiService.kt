package com.rhorbachevskyi.viewpager.domain.network

import com.rhorbachevskyi.viewpager.data.model.ContactsResponse
import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactApiService {
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") accessToken: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): UsersResponse

    @GET("users/{userId}/contacts")
    suspend fun getUserContacts(
        @Path("userId") userId: Long,
        @Header("Authorization") accessToken: String
    ): ContactsResponse

    @FormUrlEncoded
    @PUT("users/{userId}/contacts")
    suspend fun addContact(
        @Path("userId") userId: Long, @Header("Authorization") tokenHeader: String,
        @Field("contactId") contactId: Long
    ): UsersResponse

    @DELETE("users/{userId}/contacts/{contactId}")
    suspend fun deleteContact(
        @Path("userId") userId: Long, @Path("contactId") contactId: Long,
        @Header("Authorization") tokenHeader: String,
    ): UsersResponse
}