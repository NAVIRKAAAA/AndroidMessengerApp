package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repositories.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import java.util.Date
import javax.inject.Inject

class EditUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        userId: Long,
        accessToken: String,
        name: String,
        career: String?,
        phone: String,
        address: String?,
        date: Date?,
        refreshToken: String
    ): ApiState = userRepository.editUser(userId, accessToken, name, career, phone, address, date, refreshToken)
}