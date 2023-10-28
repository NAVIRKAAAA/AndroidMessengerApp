package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.ItemAddUserBinding
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces.UserItemListenerWithPagination
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.utils.ContactDiffUtil
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage

class AddContactsAdapterWithPagination(
    private val listener: UserItemListenerWithPagination
) :
    PagingDataAdapter<Contact, AddContactsAdapterWithPagination.Holder>(ContactDiffUtil()) {
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position)?:return)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddContactsAdapterWithPagination.Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddUserBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    inner class Holder(
        val binding: ItemAddUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Contact) {
            with(binding) {
                textViewName.text = user.name
                textViewCareer.text = user.career
                imageViewUserPhoto.loadImage(user.photo)
            }
            setListeners(user)
        }

        private fun setListeners(user: Contact) {
            binding.textViewAdd.setOnClickListener { listener.onClickAdd(user) }
        }
    }
}