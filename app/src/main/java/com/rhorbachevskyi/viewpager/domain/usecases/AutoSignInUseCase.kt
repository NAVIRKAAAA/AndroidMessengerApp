package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repositoriesimpl.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class AutoSignInUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        email: String, password: String
    ): ApiState = userRepository.autoLogin(email, password)
}