package com.rhorbachevskyi.viewpager.domain.usecases

import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.repositoriesimpl.ContactRepositoryImpl
import javax.inject.Inject

class GetContactByIdUseCase @Inject constructor(
    private val contactRepository: ContactRepositoryImpl
) {
    operator fun invoke(
        id: Long
    ): Contact? = contactRepository.getUserById(id)
}