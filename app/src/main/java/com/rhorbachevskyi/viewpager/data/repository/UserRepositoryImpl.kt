package com.rhorbachevskyi.viewpager.data.repository

import android.content.Context
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import java.util.Date
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userService: UserApiService
) {
     suspend fun registerUser(
        email: String,
        password: String,
        name: String,
        phone: String
    ) : ApiStateUser {
        return try {
            val response = userService.registerUser(email, password, name, phone)
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun authorizeUser(email: String, password: String): ApiStateUser {
        return try {
            val response = userService.authorizeUser(email, password)
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun editUser(
        userId: Long,
        accessToken: String,
        name: String,
        career: String?,
        phone: String,
        address: String?,
        date: Date?,
        refreshToken: String
    ): ApiStateUser {
        return try {
            val response = userService.editUser(
                userId,
                "${Constants.AUTHORIZATION_PREFIX} $accessToken",
                name,
                career,
                phone,
                address,
                date
            )
            response.data?.let {
                UserDataHolder.userData = UserResponse.Data(it.user, accessToken, refreshToken)
            }
            response.data?.let { ApiStateUser.Success(it) } ?: ApiStateUser.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.invalid_request)
        }
    }
    suspend fun autoLogin(context: Context): ApiStateUser {
        return try {
            val response = userService.authorizeUser(
                DataStore.getDataFromKey(
                    context,
                    Constants.KEY_EMAIL
                ).toString(), DataStore.getDataFromKey(context, Constants.KEY_PASSWORD).toString()
            )
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiStateUser.Success(it) }
                ?: ApiStateUser.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiStateUser.Error(R.string.automatic_login_error)
        }
    }
}