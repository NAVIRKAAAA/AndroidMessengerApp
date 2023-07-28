package com.rhorbachevskyi.viewpager.domain.useCases

import android.content.Context
import com.rhorbachevskyi.viewpager.data.repository.UserRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import javax.inject.Inject

class AutoSignInUseCase @Inject constructor(
    private val userRepository: UserRepositoryImpl
) {
    suspend operator fun invoke(
        context: Context
    ): ApiStateUser = userRepository.autoLogin(context)
}