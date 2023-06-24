package com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.ItemUserBinding
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.diff.ContactDiffCallback
import com.rhorbachevskyi.viewpager.ui.fragments.contact.contract.ContactItemClickListener
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.log

class RecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerViewAdapter.UsersViewHolder>() {

    private var listener: ContactItemClickListener? = null
    private val contacts = ArrayList<Contact>()
    private lateinit var context: Context
    private var isSelectMode = false
    private var selectItemIndex = -1
    private val selectIndexList = ArrayList<Int>()
    class UsersViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    fun setContactItemClickListener(listener: ContactItemClickListener) {
        this.listener = listener
    }

    fun setContext(context: Context) {
        this.context = context
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
            textViewName.text = contact.name
            textViewCareer.text = contact.career
            imageViewUserPhoto.loadImage(contact.photo)
        }
        setListeners(holder, contact)
    }

    override fun getItemCount(): Int = contacts.size
    fun getContactStatus(): Boolean = isSelectMode
    private fun setListeners(
        holder: UsersViewHolder,
        contact: Contact
    ) {
        if (isSelectMode) {
            setContactSelection(holder, contact)
            setSelectList(holder)
        } else {
            unSelectList(holder)
            detailView(holder, contact)
        }
        itemLongClick(holder, contact)
    }

    private fun setContactSelection(holder: UsersViewHolder, contact: Contact) {
        with(holder.binding) {
            root.setOnClickListener {
                checkboxSelectMode.isChecked = !checkboxSelectMode.isChecked
                listener?.actionWithSelectContact(contact, checkboxSelectMode.isChecked)
            }
            checkboxSelectMode.setOnClickListener {
                listener?.actionWithSelectContact(contact, checkboxSelectMode.isChecked)
            }
        }
    }

    private fun setSelectList(holder: UsersViewHolder) {
        if (selectItemIndex != -1) {
            with(holder.binding) {
                checkboxSelectMode.visibility = View.VISIBLE
                imageViewDelete.visibility = View.GONE
                checkboxSelectMode.isChecked = selectItemIndex == holder.bindingAdapterPosition
                viewBorder.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.bc_user_select_mode
                )
            }
        }
    }

    private fun unSelectList(holder: UsersViewHolder) {
        with(holder.binding) {
            checkboxSelectMode.visibility = View.GONE
            imageViewDelete.visibility = View.VISIBLE
            checkboxSelectMode.isChecked = false
            viewBorder.background = ContextCompat.getDrawable(
                context,
                R.drawable.bc_user_border
            )
        }
    }

    private fun detailView(holder: UsersViewHolder, contact: Contact) {
        with(holder.binding) {
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
        }
    }

    private fun itemLongClick(
        holder: UsersViewHolder,
        contact: Contact
    ) {
        holder.binding.itemUser.setOnLongClickListener {
            if (!isSelectMode) activateMultiselect(holder, contact) else deactivateMultiselect()
            true
        }
    }

    private fun activateMultiselect(holder: UsersViewHolder, contact: Contact) {
        isSelectMode = true
        listener?.showDeleteSelectItems()
        listener?.actionWithSelectContact(contact, true)
        listener?.hideNotSelectModeText()
        selectItemIndex = holder.bindingAdapterPosition
        notifyDataSetChanged()
    }

    fun deactivateMultiselect() {
        isSelectMode = false
        listener?.hideImageDeleteBin()
        listener?.showNotSelectModeText()
        selectItemIndex = -1
        notifyItemRangeChanged( 0, contacts.size)
    }

    fun updateContacts(newUsers: ArrayList<Contact>) {
        val diffResult = DiffUtil.calculateDiff(ContactDiffCallback(contacts, newUsers))
        contacts.clear()
        contacts.addAll(newUsers)
        diffResult.dispatchUpdatesTo(this)
    }

    private fun setTransitionName(view: View, name: String): Pair<View, String> {
        view.transitionName = name
        return view to name
    }
}