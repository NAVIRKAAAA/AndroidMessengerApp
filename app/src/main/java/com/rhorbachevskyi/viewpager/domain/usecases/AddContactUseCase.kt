package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repositoriesimpl.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class AddContactUseCase @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        accessToken: String,
        userId: Long,
        contactId: Long,
    ): ApiState = contactRepository.addContact(accessToken, userId, contactId)
}
