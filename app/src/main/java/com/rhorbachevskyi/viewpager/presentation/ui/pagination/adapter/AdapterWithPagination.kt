package com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.utils.ContactDiffUtil
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.interfaces.ClickListenerWithPagination
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage

class AdapterWithPagination(private val listener: ClickListenerWithPagination) :
    PagingDataAdapter<Contact, AdapterWithPagination.UsersViewHolder>(ContactDiffUtil()) {

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    inner class UsersViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            with(binding) {
                textViewName.text = contact.name
                textViewCareer.text = contact.career
                imageViewUserPhoto.loadImage(contact.photo)
                imageViewDelete.invisible()
            }
            setListeners()
        }

        private fun setListeners() {
            deleteItem()
            with(binding) {
                itemUser.setOnLongClickListener {
                    listener.onActionClick()
                    true
                }
                root.setOnClickListener { listener.onActionClick() }
            }
        }

        private fun deleteItem() {
            binding.imageViewDelete.setOnClickListener { listener.onActionClick() }
        }
    }
}