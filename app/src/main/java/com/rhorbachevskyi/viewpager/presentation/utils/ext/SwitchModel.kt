package com.rhorbachevskyi.viewpager.presentation.utils.ext

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserData

fun Contact.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        name = name,
        career = career,
        address = address,
    )
}

fun ContactEntity.fromEntity(): Contact {
    return Contact(
        id = id,
        name = name,
        career = career,
        address = address,
    )
}