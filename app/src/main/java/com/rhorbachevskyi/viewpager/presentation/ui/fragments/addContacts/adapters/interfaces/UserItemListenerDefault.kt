package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.data.model.Contact

interface UserItemListenerDefault {
    fun onClickAdd(contact: Contact)
    fun onClickContact(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}