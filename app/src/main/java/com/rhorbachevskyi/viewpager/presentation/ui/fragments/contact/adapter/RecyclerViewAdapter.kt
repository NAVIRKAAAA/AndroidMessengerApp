package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.interfaces.ContactItemClickListener
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.utils.ContactDiffUtil
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible

class RecyclerViewAdapter(private val listener: ContactItemClickListener) :
    ListAdapter<Contact, RecyclerViewAdapter.UsersViewHolder>(ContactDiffUtil()) {
    private var isSelectItems: ArrayList<Pair<Boolean, Int>> = ArrayList()
    var isMultiselectMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class UsersViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            with(binding) {
                textViewName.text = contact.name
                textViewCareer.text = contact.career
                imageViewUserPhoto.loadImage(contact.photo)
            }
            setListeners(contact)
        }

        private fun setListeners(
            contact: Contact
        ) {
            if (isMultiselectMode) setSelectList(contact) else deleteItem(contact)
            with(binding) {
                itemUser.setOnLongClickListener {
                    itemLongClick(contact)
                    true
                }
                root.setOnClickListener { itemClick(contact) }
            }
        }

        private fun deleteItem(contact: Contact) {
            binding.imageViewDelete.setOnClickListener {
                listener.onClickDelete(contact)
            }
        }

        private fun itemClick(contact: Contact) {
            with(binding) {
                if (isMultiselectMode) checkboxSelectMode.isChecked =
                    !checkboxSelectMode.isChecked
                listener.onClickContact(
                    contact, arrayOf(
                        setTransitionName(
                            imageViewUserPhoto,
                            Constants.TRANSITION_NAME_IMAGE + contact.id
                        ),
                        setTransitionName(
                            textViewName,
                            Constants.TRANSITION_NAME_CONTACT_NAME + contact.id
                        ), setTransitionName(
                            textViewCareer,
                            Constants.TRANSITION_NAME_CAREER + contact.id
                        )
                    )
                )
            }
        }

        private fun setSelectList(contact: Contact) {
            with(binding) {
                checkboxSelectMode.visible()
                imageViewDelete.gone()
                checkboxSelectMode.isChecked =
                    isSelectItems.find { it.second == contact.id.toInt() }?.first == true
                viewBorder.background = ContextCompat.getDrawable(
                    root.context,
                    R.drawable.bc_user_select_mode
                )
            }
        }

        private fun itemLongClick(contact: Contact) {
            listener.onLongClick(contact)
        }

        private fun setTransitionName(view: View, name: String): Pair<View, String> {
            view.transitionName = name
            return view to name
        }
    }

    fun setMultiselectData(isMultiselectItem: ArrayList<Pair<Boolean, Int>>) {
        this.isSelectItems = isMultiselectItem
    }
}