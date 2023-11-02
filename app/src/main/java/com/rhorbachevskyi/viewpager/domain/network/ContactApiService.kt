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

interface ContactApiService {
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") accessToken: String,
    ): UsersResponse

    @GET("users/{userId}/contacts")
    suspend fun getUserContacts(
        @Header("Authorization") accessToken: String,
        @Path("userId") userId: Long
    ): ContactsResponse

    @FormUrlEncoded
    @PUT("users/{userId}/contacts")
    suspend fun addContact(
        @Header("Authorization") tokenHeader: String,
        @Path("userId") userId: Long,
        @Field("contactId") contactId: Long
    ): UsersResponse

    @DELETE("users/{userId}/contacts/{contactId}")
    suspend fun deleteContact(
        @Header("Authorization") tokenHeader: String,
        @Path("userId") userId: Long,
        @Path("contactId") contactId: Long,
    ): UsersResponse
}