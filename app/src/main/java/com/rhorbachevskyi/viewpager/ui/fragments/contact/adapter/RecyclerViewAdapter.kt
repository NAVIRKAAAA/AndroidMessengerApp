package com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.domain.repository.ContactItemClickListener
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.utils.Constants


class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerViewAdapter.UsersViewHolder>() {

    private var listener: ContactItemClickListener? = null
    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    private val contacts = ArrayList<Contact>()

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
                listener?.onOpenNewFragment(contact, arrayOf(
                    setTransitionName(
                        imageViewUserPhoto,
                        Constants.TRANSITION_NAME_IMAGE + contact.id
                    ),
                    setTransitionName(
                        textViewName,
                        Constants.TRANSITION_NAME_NAME + contact.id
                    ),setTransitionName(
                        textViewCareer,
                        Constants.TRANSITION_NAME_CAREER + contact.id
                    )
                ))
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