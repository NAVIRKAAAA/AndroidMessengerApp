package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.repositories.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class UsersUseCase @Inject constructor( // TODO: think
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        accessToken: String,
        user: UserData
    ): ApiState  = contactRepository.getUsers(accessToken, user, 0,0)
}