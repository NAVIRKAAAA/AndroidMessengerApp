package com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.rhorbachevskyi.viewpager.domain.repository.ContactItemClickListener
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.databinding.SelectItemUserBinding
import com.rhorbachevskyi.viewpager.ui.fragments.contact.viewholders.ContactsSelectViewHolder
import com.rhorbachevskyi.viewpager.ui.fragments.contact.viewholders.ContactsViewHolder

class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: ContactItemClickListener? = null

    private val contacts = ArrayList<Contact>()
    private var isSelectMode = false

    fun setContactItemClickListener(listener: ContactItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (isSelectMode) {
            val inflater = LayoutInflater.from(parent.context)
            val selectBinding = SelectItemUserBinding.inflate(inflater, parent, false)
            return ContactsSelectViewHolder(selectBinding)
        }
        val inflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemUserBinding.inflate(inflater, parent, false)
        return ContactsViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ContactsViewHolder) {
            val contact = contacts[position]
            holder.bindTo(contact, listener, holder)
        } else if (holder is ContactsSelectViewHolder) {
            val contact = contacts[position]
            holder.bindTo(contact)
        }
    }

//    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
//        val contact = contacts[position]
//        val layoutResId = if (isSelectMode) R.layout.select_item_user else R.layout.item_user
//        with(holder.binding) {
//            imageViewDelete.setOnClickListener {
//                listener?.onUserDelete(contact, holder.bindingAdapterPosition)
//            }
//            itemUser.setOnClickListener {
//                listener?.onOpenNewFragment(
//                    contact, arrayOf(
//                        setTransitionName(
//                            imageViewUserPhoto,
//                            Constants.TRANSITION_NAME_IMAGE + contact.id
//                        ),
//                        setTransitionName(
//                            textViewName,
//                            Constants.TRANSITION_NAME_NAME + contact.id
//                        ), setTransitionName(
//                            textViewCareer,
//                            Constants.TRANSITION_NAME_CAREER + contact.id
//                        )
//                    )
//                )
//            }
//
//            itemUser.setOnLongClickListener {
//                isSelectMode = true
//                listener?.showImageDeleteBin()
//                true
//            }
//            textViewName.text = contact.name
//            textViewCareer.text = contact.career
//            imageViewUserPhoto.loadImage(contact.photo)
//
//        }
//    }

    override fun getItemCount(): Int = contacts.size

    fun updateContacts(newUsers: ArrayList<Contact>) {
        contacts.clear()
        contacts.addAll(newUsers)
    }


}