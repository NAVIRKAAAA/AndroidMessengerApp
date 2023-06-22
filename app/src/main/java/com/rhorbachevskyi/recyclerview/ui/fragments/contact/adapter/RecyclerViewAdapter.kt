package com.rhorbachevskyi.recyclerview.ui.fragments.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.contract.ContactItemClickListener
import com.example.recyclerview.databinding.ItemUserBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.Constants
import com.rhorbachevskyi.recyclerview.utils.ext.loadImage


class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerViewAdapter.UsersViewHolder>() {

    private var listener: ContactItemClickListener? = null
    private val contacts = ArrayList<Contact>()

    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    fun setContactItemClickListener(listener: ContactItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            imageViewDelete.setOnClickListener {
                listener?.onUserDelete(contact, holder.bindingAdapterPosition)
            }
            itemUser.setOnClickListener {
                listener?.onOpenNewFragment(
                    contact, arrayOf(
                        setTransitionName(
                            imageViewUserPhoto,
                            Constants.TRANSITION_NAME_IMAGE + contact.id
                        ),
                        setTransitionName(
                            textViewName,
                            Constants.TRANSITION_NAME_NAME + contact.id
                        ), setTransitionName(
                            textViewCareer,
                            Constants.TRANSITION_NAME_CAREER + contact.id
                        )
                    )
                )
            }
            textViewName.text = contact.name
            textViewCareer.text = contact.career
            imageViewUserPhoto.loadImage(contact.photo)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newUsers: ArrayList<Contact>) {
        contacts.clear()
        contacts.addAll(newUsers)
    }

    private fun setTransitionName(view: View, name: String): Pair<View, String> {
        view.transitionName = name
        return view to name
    }
}