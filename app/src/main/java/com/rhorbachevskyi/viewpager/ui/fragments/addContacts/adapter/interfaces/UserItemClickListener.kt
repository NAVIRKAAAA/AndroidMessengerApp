package com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.data.model.Contact

interface UserItemClickListener {
    fun onClickAdd(contact: Contact)
    fun onClickContact(contact: Contact, transitionPairs: Array<Pair<View, String>>)
    fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}