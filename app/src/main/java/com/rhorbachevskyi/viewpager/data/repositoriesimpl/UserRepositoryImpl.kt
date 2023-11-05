package com.rhorbachevskyi.viewpager.data.repositoriesimpl

import android.content.Context
import com.rhorbachevskyi.viewpager.data.repositoriesimpl.HandleError.getErrorMessage
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.domain.network.UserApiService
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.getStringFromPrefs
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
    ): ApiState {
        val code: Int
        return try {
            val response = userService.registerUser(email, password, name, phone)
            code = response.code
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(getErrorMessage(code))
        } catch (e: Exception) {
            ApiState.Error(getErrorMessage(400))
        }
    }

    suspend fun authorizeUser(email: String, password: String): ApiState {
        val code: Int
        return try {
            val response = userService.authorizeUser(email, password)
            code = response.code
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(getErrorMessage(code))
        } catch (e: Exception) {
            ApiState.Error(getErrorMessage(400))
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
        val code: Int
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
            code = response.code
            response.data?.let {
                UserDataHolder.userData = UserResponse.Data(it.user, accessToken, refreshToken)
            }
            response.data?.let { ApiState.Success(it) } ?: ApiState.Error(
                getErrorMessage(code)
            )
        } catch (e: Exception) {
            ApiState.Error(getErrorMessage(400))
        }
    }

    suspend fun autoLogin(context: Context): ApiState {
        val code: Int
        return try {
            val response = userService.authorizeUser(
                context.getStringFromPrefs(Constants.KEY_EMAIL),
                context.getStringFromPrefs(Constants.KEY_PASSWORD)
            )
            code = response.code
            response.data?.let { UserDataHolder.userData = it }
            response.data?.let { ApiState.Success(it) }
                ?: ApiState.Error(getErrorMessage(code))
        } catch (e: Exception) {
            ApiState.Error(getErrorMessage(400))
        }
    }
}