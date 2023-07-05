package com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.ItemAddUserBinding
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.utils.ContactDiffUtil
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.visible

class AddContactsAdapter(private val listener: UserItemClickListener) :
    ListAdapter<Contact, AddContactsAdapter.UsersViewHolder>(ContactDiffUtil()) {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class UsersViewHolder(private val binding: ItemAddUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            with(binding) {
                textViewName.text = contact.name
                textViewCareer.text = contact.career
                imageViewUserPhoto.loadImage(contact.photo)
            }
            setListeners(contact)
        }

        private fun setListeners(contact: Contact) {
            binding.textViewAdd.setOnClickListener {
                binding.textViewAdd.invisible()
                binding.progressBar.visible()
                listener.onClickAdd(contact)
            }
        }
        fun addedSuccessfully() {

        }
    }

}


