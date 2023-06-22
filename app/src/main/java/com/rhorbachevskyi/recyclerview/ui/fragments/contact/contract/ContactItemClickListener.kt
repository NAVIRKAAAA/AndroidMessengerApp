package com.rhorbachevskyi.recyclerview.ui.fragments.contact.contract

import android.view.View
import com.rhorbachevskyi.recyclerview.domain.model.Contact

interface ContactItemClickListener {
    fun onUserDelete(contact: Contact, position: Int)
    fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>)
}
