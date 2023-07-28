package com.rhorbachevskyi.viewpager.domain.useCases

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repository.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import javax.inject.Inject

class DeleteContactUseCase @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        userId: Long,
        contact: Contact,
        accessToken: String
    ): ApiStateUser = contactRepository.deleteContact(userId, accessToken, contact)
}
