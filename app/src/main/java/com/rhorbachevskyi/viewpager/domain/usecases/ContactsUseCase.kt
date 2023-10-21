package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repositories.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class ContactsUseCase  @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        userId: Long,
        accessToken: String
    ): ApiState = contactRepository.getContacts(userId, accessToken)
}