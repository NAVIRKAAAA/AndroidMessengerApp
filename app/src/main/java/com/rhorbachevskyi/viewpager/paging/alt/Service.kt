package com.rhorbachevskyi.viewpager.paging.alt

import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface Service {
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") accessToken: String,
    ): UsersResponse?
}