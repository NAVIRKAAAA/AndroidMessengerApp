package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.repository.ContactRepositoryImpl
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import javax.inject.Inject

class ContactsUseCase  @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    suspend operator fun invoke(
        userId: Long,
        accessToken: String
    ): ApiStateUser = contactRepository.getContacts(userId, accessToken)
}