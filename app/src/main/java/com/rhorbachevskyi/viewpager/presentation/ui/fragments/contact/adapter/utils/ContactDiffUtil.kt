package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.utils

import androidx.recyclerview.widget.DiffUtil
import com.rhorbachevskyi.viewpager.data.model.Contact

class ContactDiffUtil : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem.id == newItem.id
}