package com.rhorbachevskyi.viewpager.ui.fragments.contact.contract

import android.view.View
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.RecyclerViewAdapter

interface ContactItemClickListener {
    fun onUserDelete(contact: Contact, position: Int)
    fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>)
    fun showDeleteSelectItems()
    fun hideImageDeleteBin()
    fun showNotSelectModeText()
    fun hideNotSelectModeText()
    fun actionWithSelectContact(contact: Contact, checked: Boolean)
    fun getSelectList(): ArrayList<Contact>
}
