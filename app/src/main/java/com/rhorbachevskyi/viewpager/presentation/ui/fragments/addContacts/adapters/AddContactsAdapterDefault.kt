package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.ItemAddUserBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.utils.ContactDiffUtil
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible

class AddContactsAdapterDefault(private val listener: UserItemClickListener) :
    ListAdapter<Contact, AddContactsAdapterDefault.UsersViewHolder>(ContactDiffUtil()) {
    private var states: ArrayList<Pair<Long, ApiState>> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        holder.bind(
            getItem(position)?:return,
            states.find { it.first == getItem(position)?.id }?.second ?: ApiState.Initial
        )
    }

    inner class UsersViewHolder(private val binding: ItemAddUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact, state: ApiState) {
            with(binding) {
                textViewName.text = contact.name
                textViewCareer.text = contact.career
                imageViewUserPhoto.loadImage(contact.photo)
            }
            setState(state)
            setListeners(contact)
        }

        private fun setState(state: ApiState) {
            with(binding) {
                when (state) {
                    is ApiState.Success<*> -> {
                        textViewAdd.gone()
                        progressBar.gone()
                        imageViewDoneAddContact.visible()
                    }

                    is ApiState.Initial -> {
                        textViewAdd.visible()
                        progressBar.gone()
                        imageViewDoneAddContact.invisible()
                    }

                    is ApiState.Loading -> {
                        textViewAdd.gone()
                        progressBar.visible()
                        imageViewDoneAddContact.invisible()
                    }

                    is ApiState.Error -> Unit
                }
            }
        }


        private fun setListeners(contact: Contact) {
            with(binding) {
                textViewAdd.setOnClickListener { addContact(contact) }
                root.setOnClickListener { detailView(contact) }
            }
        }

        private fun addContact(contact: Contact) {
            listener.onAddClick(contact)
        }
        private fun detailView(contact: Contact) {
            with(binding) {
                listener.onContactClick(
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


        private fun setTransitionName(view: View, name: String): Pair<View, String> {
            view.transitionName = name
            return view to name
        }
    }

    fun setStates(states: ArrayList<Pair<Long, ApiState>>) {

        if (this.states.size != states.size) {
            this.states = states
            val lastIndex = currentList.indexOfLast { it.id == states.lastOrNull()?.first }
            if (lastIndex != -1) {
                notifyItemChanged(lastIndex)
            }
            return
        }
        states.forEachIndexed { index, state ->
            if (this.states[index] != states[index]) {
                this.states[index] = state
                notifyItemChanged(currentList.indexOfFirst { it.id == state.first })
            }
        }
    }
}


