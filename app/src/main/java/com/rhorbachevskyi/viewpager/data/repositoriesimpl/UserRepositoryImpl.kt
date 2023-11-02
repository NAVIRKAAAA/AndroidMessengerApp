package com.rhorbachevskyi.viewpager.data.repositoriesimpl

import android.content.Context
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiState
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
    ) : ApiState {
        return try {
            val response = userService.registerUser(email, password, name, phone)
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiState.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun authorizeUser(email: String, password: String): ApiState {
        return try {
            val response = userService.authorizeUser(email, password)
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiState.Error(R.string.register_error_user_exist)
        }
    }

    suspend fun editUser(
        accessToken: String,
        userId: Long,
        name: String,
        career: String?,
        phone: String,
        address: String?,
        date: Date?,
        refreshToken: String
    ): ApiState {
        return try {
            val response = userService.editUser(
                "${Constants.AUTH_PREFIX} $accessToken",
                userId,
                name,
                career,
                phone,
                address,
                date
            )
            response.data?.let {
                UserDataHolder.userData = UserResponse.Data(it.user, accessToken, refreshToken)
            }
            response.data?.let { ApiState.Success(it) } ?: ApiState.Error(
                R.string.invalid_request
            )
        } catch (e: Exception) {
            ApiState.Error(R.string.invalid_request)
        }
    }
    suspend fun autoLogin(context: Context): ApiState {
        return try {
            val response = userService.authorizeUser(
                DataStore.getDataFromKey(
                    context,
                    Constants.KEY_EMAIL
                ).toString(), DataStore.getDataFromKey(context, Constants.KEY_PASSWORD).toString()
            )
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(R.string.invalid_request)
        } catch (e: Exception) {
            ApiState.Error(R.string.automatic_login_error)
        }
    }
}