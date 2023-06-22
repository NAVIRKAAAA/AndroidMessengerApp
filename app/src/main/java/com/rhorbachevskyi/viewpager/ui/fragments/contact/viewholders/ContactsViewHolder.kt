package com.rhorbachevskyi.viewpager.ui.fragments.contact.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.domain.repository.ContactItemClickListener
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.loadImage

class ContactsViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bindTo(contact: Contact, listener: ContactItemClickListener?, holder: ContactsViewHolder,) {
        with(binding) {
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
            itemUser.setOnLongClickListener {
                listener?.showImageDeleteBin()
                true
            }
            textViewName.text = contact.name
            textViewCareer.text = contact.career
            imageViewUserPhoto.loadImage(contact.photo)
        }
    }
    private fun setTransitionName(view: View, name: String): Pair<View, String> {
        view.transitionName = name
        return view to name
    }
}