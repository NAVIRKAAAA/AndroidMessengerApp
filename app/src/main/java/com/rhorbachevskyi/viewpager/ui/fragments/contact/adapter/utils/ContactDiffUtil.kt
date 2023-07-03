package com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.utils

import androidx.recyclerview.widget.DiffUtil
import com.rhorbachevskyi.viewpager.data.model.Contact

class ContactDiffUtil : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }
}