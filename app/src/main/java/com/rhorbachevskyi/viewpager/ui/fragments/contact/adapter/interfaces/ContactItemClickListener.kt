package com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.domain.model.Contact

interface ContactItemClickListener {
    fun onClickDelete(contact: Contact)
    fun onClickContact(contact: Contact,transitionPairs: Array<Pair<View, String>>)
    fun onLongClick(contact: Contact)
    fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}
