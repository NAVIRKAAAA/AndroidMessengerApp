package com.rhorbachevskyi.viewpager.domain.network

import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersRepository {
    @GET("users")
    suspend fun getPageUsers(
        @Query("page") page: Int
    ): UsersResponse
}