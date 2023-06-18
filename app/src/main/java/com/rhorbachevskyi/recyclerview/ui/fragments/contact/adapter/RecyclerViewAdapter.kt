package com.rhorbachevskyi.recyclerview.ui.fragments.contact.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.recyclerview.repository.ContactItemClickListener
import com.example.recyclerview.databinding.ItemUserBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.ext.loadImage


class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerViewAdapter.UsersViewHolder>() {

    private var listener: ContactItemClickListener? = null

    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    private val users = ArrayList<Contact>()

    fun setUserItemClickListener(listener: ContactItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserBinding.inflate(inflater, parent, false)
        return UsersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val user = users[position]
        with(holder.binding) {
            imageViewDelete.setOnClickListener {
                listener?.onUserDelete(user, holder.bindingAdapterPosition)
            }
            itemUser.setOnClickListener {
                listener?.onOpenNewFragment(user)

            }
            textViewName.text = user.name
            textViewCareer.text = user.career
            imageViewUserPhoto.loadImage(user.photo)

        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: ArrayList<Contact>) {
        users.clear()
        users.addAll(newUsers)
    }
}