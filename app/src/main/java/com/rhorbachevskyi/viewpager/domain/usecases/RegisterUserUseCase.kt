package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repository.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        phone: String
    ): ApiStateUser = userRepository.registerUser(email, password, name, phone)
}