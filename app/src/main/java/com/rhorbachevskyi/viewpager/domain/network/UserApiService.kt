package com.rhorbachevskyi.viewpager.domain.network

import com.rhorbachevskyi.viewpager.data.model.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

interface UserApiService {
    @POST("users")
    suspend fun registerUser(
        @Query("email") email: String,
        @Query("password") password: String,
        @Query("name") name: String,
        @Query("phone") phone: String
    ): UserResponse

    @POST("login")
    suspend fun authorizeUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): UserResponse

    @FormUrlEncoded
    @PUT("users/{userId}")
    suspend fun editUser(
        @Header("Authorization") tokenHeader: String,
        @Path("userId") id: Long,
        @Field("name") name: String,
        @Field("career") career: String?,
        @Field("phone") phone: String,
        @Field("address") address: String?,
        @Field("birthday") birthday: Date?
    ): UserResponse
}