package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repository.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
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
    ): ApiStateUser = userRepository.editUser(userId, accessToken, name, career, phone, address, date, refreshToken)
}