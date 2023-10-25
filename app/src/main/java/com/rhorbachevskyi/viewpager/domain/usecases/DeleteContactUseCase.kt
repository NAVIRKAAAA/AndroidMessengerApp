package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repositories.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        userId: Long,
        contact: Contact,
        accessToken: String
    ): ApiState = contactRepository.deleteContact(userId, accessToken, contact)
}