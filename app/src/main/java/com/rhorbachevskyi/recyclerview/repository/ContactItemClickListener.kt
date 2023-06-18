package com.rhorbachevskyi.recyclerview.repository

import com.rhorbachevskyi.recyclerview.domain.model.Contact

interface ContactItemClickListener {
    fun onUserDelete(user: Contact, position: Int)
    fun onOpenNewFragment(user: Contact)
}
