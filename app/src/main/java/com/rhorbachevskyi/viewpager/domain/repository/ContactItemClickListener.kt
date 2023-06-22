package com.rhorbachevskyi.viewpager.domain.repository

import android.view.View
import com.rhorbachevskyi.viewpager.domain.model.Contact

interface ContactItemClickListener {
    fun onUserDelete(contact: Contact, position: Int)
    fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>)
    fun showImageDeleteBin()
    fun hideImageDeleteBin()
}
