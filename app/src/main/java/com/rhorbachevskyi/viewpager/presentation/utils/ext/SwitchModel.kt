package com.rhorbachevskyi.viewpager.presentation.utils.ext

import com.rhorbachevskyi.viewpager.data.database.entity.ContactEntity
import com.rhorbachevskyi.viewpager.data.model.Contact

fun Contact.toEntity(): ContactEntity = ContactEntity(
    id = id,
    name = name,
    career = career,
    address = address,
)


fun ContactEntity.fromEntity(): Contact = Contact(
    id = id,
    name = name,
    career = career,
    address = address,
)
