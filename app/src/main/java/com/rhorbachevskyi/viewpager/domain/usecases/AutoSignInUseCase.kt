package com.rhorbachevskyi.viewpager.domain.usecases

import android.content.Context
import com.rhorbachevskyi.viewpager.data.repositoriesimpl.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class AutoSignInUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        context: Context
    ): ApiState = userRepository.autoLogin(context)
}