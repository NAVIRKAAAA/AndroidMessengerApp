package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces

import android.view.View
import com.rhorbachevskyi.viewpager.data.model.Contact

interface UserItemClickListener {
    fun onAddClick(contact: Contact)
    fun onContactClick(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}