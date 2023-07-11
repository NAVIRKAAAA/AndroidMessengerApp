package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.data.model.Contact

interface UserItemClickListener {
    fun onClickAdd(contact: Contact)
    fun onClickContact(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}