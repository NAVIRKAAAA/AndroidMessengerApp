package com.rhorbachevskyi.viewpager.domain.network

import androidx.paging.PagingData
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UsersRepository {
    @GET("users")
    fun getPagedUsers(
        @Header("Authorization") accessToken: String,
        @Query("user") user: UserData
    ): Flow<PagingData<Contact>>
}