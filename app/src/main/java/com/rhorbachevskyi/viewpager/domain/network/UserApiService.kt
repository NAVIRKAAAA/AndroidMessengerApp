package com.rhorbachevskyi.viewpager.domain.network

import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Date

interface UserApiService {
    @POST("users")
    suspend fun registerUser(@Body body: UserRequest): UserResponse

    @POST("login")
    suspend fun authorizeUser(@Body body: UserRequest): UserResponse

    @GET("users/{userId}")
    suspend fun getUser(
        @Path("userId") userId: Long,
        @Header("Authorization") accessToken: String
    ): UserResponse

    @FormUrlEncoded
    @PUT("users/{userId}")
    suspend fun editUser(
        @Path("userId") id: Long, @Header("Authorization") tokenHeader: String,
        @Field("name") name: String, @Field("career") career: String?,
        @Field("phone") phone: String, @Field("address") address: String?,
        @Field("birthday") birthday: Date?
    ): UserResponse
}