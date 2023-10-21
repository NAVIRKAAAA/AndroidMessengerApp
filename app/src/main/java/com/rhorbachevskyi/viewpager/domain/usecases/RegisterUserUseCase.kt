package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repositories.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        phone: String
    ): ApiState = userRepository.registerUser(email, password, name, phone)
}