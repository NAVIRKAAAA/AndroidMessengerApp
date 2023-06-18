package com.rhorbachevskyi.recyclerview.domain.model

import java.util.UUID

data class Contact(
    val name: String,
    val career: String,
    val photo: String = "",
    val id: UUID = UUID.randomUUID()
)
