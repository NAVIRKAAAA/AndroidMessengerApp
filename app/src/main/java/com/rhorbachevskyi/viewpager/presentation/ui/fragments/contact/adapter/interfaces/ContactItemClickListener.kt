package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.data.model.Contact

interface ContactItemClickListener {
    fun onDeleteClick(contact: Contact)
    fun onContactClick(contact: Contact, transitionPairs: Array<Pair<View, String>>)
    fun onLongClick(contact: Contact)
}