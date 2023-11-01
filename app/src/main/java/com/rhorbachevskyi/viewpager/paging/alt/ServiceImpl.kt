package com.rhorbachevskyi.viewpager.paging.alt

import com.rhorbachevskyi.viewpager.data.model.UsersResponse
import com.rhorbachevskyi.viewpager.domain.network.ContactApiService
import javax.inject.Inject

class ServiceImpl @Inject constructor(
    private val contactService: ContactApiService,
) : Service {

    //    override suspend fun getUsers(
//        accessToken: String,
//    ): UsersResponse {
//        return try {
//            val response =
//                contactService.getUsers(accessToken = "${Constants.AUTHORIZATION_PREFIX} $accessToken")
//            response
//        } catch (e: Exception) {
//            val response =
//                contactService.getUsers(accessToken = "${Constants.AUTHORIZATION_PREFIX} $accessToken")
//            response
//        }
//    }
    override suspend fun getUsers(accessToken: String): UsersResponse? {
        return null
    }
}