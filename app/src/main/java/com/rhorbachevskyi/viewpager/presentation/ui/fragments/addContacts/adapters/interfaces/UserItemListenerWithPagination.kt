package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces

import com.rhorbachevskyi.viewpager.data.model.Contact

interface UserItemListenerWithPagination {
    fun onClickAdd(contact: Contact)
}