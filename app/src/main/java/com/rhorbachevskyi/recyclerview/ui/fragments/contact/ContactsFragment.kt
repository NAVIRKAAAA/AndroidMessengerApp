package com.rhorbachevskyi.recyclerview.ui.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.recyclerview.domain.repository.ContactItemClickListener
import com.rhorbachevskyi.recyclerview.ui.fragments.dialog.DialogFragment
import com.example.recyclerview.databinding.FragmentContactsBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.Constants
import com.google.android.material.snackbar.Snackbar

class ContactsFragment : Fragment(), ContactItemClickListener {
    private lateinit var binding: FragmentContactsBinding
    private val adapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter()
    }
    private var userViewModel = ContactsViewModel()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialRecyclerview()
        setClickListener()
    }

    private fun initialRecyclerview() {
        setTouchRecycleItemListener()
        userViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        val layoutManager = LinearLayoutManager(context)
        adapter.setContactItemClickListener(this)
        binding.recyclerViewContacts.layoutManager = layoutManager
        binding.recyclerViewContacts.adapter = adapter
        adapter.updateContacts(userViewModel.getContactsList())
    }

    private fun setClickListener() {
        showAddContactsDialog()
    }

    private fun showAddContactsDialog() {
        binding.textViewAddContacts.setOnClickListener {
            val dialogFragment = DialogFragment()
            dialogFragment.setViewModel(userViewModel)
            dialogFragment.setAdapter(adapter)
            dialogFragment.show(parentFragmentManager, Constants.DIALOG_TAG)
        }
    }

    private fun setTouchRecycleItemListener() {
        val itemTouchCallback = setTouchCallBackListener()
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.recyclerViewContacts)
    }

    private fun setTouchCallBackListener(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteUserWithRestore(
                    userViewModel.getContactsList()[viewHolder.adapterPosition],
                    viewHolder.adapterPosition
                )
            }
        }
    }

    override fun onUserDelete(contact: Contact, position: Int) {
        deleteUserWithRestore(contact, position)
    }

    override fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>){
        val direction = ContactsFragmentDirections.actionContactsFragmentToProfileFragment(contact)
        val extras = FragmentNavigatorExtras(*transitionPairs)
        findNavController().navigate(direction, extras)
    }

    fun deleteUserWithRestore(user: Contact, position: Int) {
        if (userViewModel.deleteContact(user)) {
            adapter.notifyItemRemoved(position)
            adapter.updateContacts(userViewModel.getContactsList())
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(user.name),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.restore)) {
                    if (userViewModel.addContact(user, position)) {
                        adapter.notifyItemInserted(position)
                        adapter.updateContacts(userViewModel.getContactsList())
                    }
                }.show()
        }
    }
}