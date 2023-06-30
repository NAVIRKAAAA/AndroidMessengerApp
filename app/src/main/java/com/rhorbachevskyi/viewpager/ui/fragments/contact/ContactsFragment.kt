package com.rhorbachevskyi.viewpager.ui.fragments.contact

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.viewpager.ui.fragments.contact.contract.ContactItemClickListener
import com.rhorbachevskyi.viewpager.ui.fragments.dialog.DialogFragment
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentContactsBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.utils.Constants

class ContactsFragment :BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate), ContactItemClickListener {

    private val adapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter()
    }
    private var contactsViewModel = ContactsViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialRecyclerview()
        setClickListener()
    }
    override fun showDeleteSelectItems() {
        binding.imageViewDeleteSelectMode.visibility = View.VISIBLE
    }


    override fun onUserDelete(contact: Contact, position: Int) {
        deleteUserWithRestore(contact, position)
    }

    override fun onOpenNewFragment(contact: Contact, transitionPairs: Array<Pair<View, String>>) {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToContactProfile(contact)
        val extras = FragmentNavigatorExtras(*transitionPairs)
        navController.navigate(direction, extras)
    }

    override fun hideImageDeleteBin() {
        binding.imageViewDeleteSelectMode.visibility = View.GONE
    }

    override fun showNotSelectModeText() {
        binding.textViewAddContacts.visibility = View.VISIBLE
    }

    override fun hideNotSelectModeText() {
        binding.textViewAddContacts.visibility = View.GONE
    }

    override fun getSelectList() : ArrayList<Contact> {
        return contactsViewModel.getSelectedContactsList()
    }
    override fun actionWithSelectContact(
        contact: Contact,
        checked: Boolean
    ) {
        if (checked && !contactsViewModel.getSelectedContactsList().contains(contact)) {
            contactsViewModel.addSelectContact(contact)
        }
        if (!checked && contactsViewModel.getSelectedContactsList().contains(contact)) {
            contactsViewModel.deleteSelectContact(contact)
        }
        if (contactsViewModel.getSelectedContactsList().size == 0) {
            adapter.deactivateMultiselect()
        }
    }

    private fun initialRecyclerview() {
        contactsViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        val layoutManager = LinearLayoutManager(context)
        adapter.setContactItemClickListener(this)
        if(contactsViewModel.getSelectedContactsList().size != 0) adapter.setModeAfterTurnScreen()
        binding.recyclerViewContacts.layoutManager = layoutManager
        binding.recyclerViewContacts.adapter = adapter
        adapter.setContext(requireContext())
        adapter.updateContacts(contactsViewModel.getContactsList())
        setTouchRecycleItemListener()
    }

    private fun setClickListener() {
        showAddContactsDialog()
        navigationBack()
        deleteSelectedContacts()
    }

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(0)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireParentFragment() as? ViewPagerFragment)?.openFragment(0)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun deleteSelectedContacts() {
        binding.imageViewDeleteSelectMode.setOnClickListener {
            val size = contactsViewModel.getSelectedContactsList().size
            for (i in contactsViewModel.getSelectedContactsList().size - 1 downTo 0) {
                val contact = contactsViewModel.getSelectedContactsList()[i]
                contactsViewModel.deleteSelectContact(contact)
                contactsViewModel.deleteContact(contact)
                adapter.updateContacts(contactsViewModel.getContactsList())
            }
            adapter.deactivateMultiselect()
            Snackbar.make(
                binding.recyclerViewContacts,
                if(size > 1) getString(R.string.contacts_removed) else getString(R.string.contact_removed),
                Snackbar.LENGTH_LONG
            ).show()
        }

    }

    private fun showAddContactsDialog() {
        binding.textViewAddContacts.setOnClickListener {
            val dialogFragment = DialogFragment()
            dialogFragment.setViewModel(contactsViewModel)
            dialogFragment.setAdapter(adapter)
            dialogFragment.show(parentFragmentManager, Constants.DIALOG_TAG)
        }
    }

    private fun setTouchRecycleItemListener() {
        val itemTouchCallback = setTouchCallBackListener()
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.recyclerViewContacts)
    }

    private fun setTouchCallBackListener(): ItemTouchHelper.Callback {
        return object : ItemTouchHelper.SimpleCallback(0, 0) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (!adapter.getContactStatus()) ItemTouchHelper.LEFT else 0
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteUserWithRestore(
                    contactsViewModel.getContactsList()[viewHolder.bindingAdapterPosition],
                    viewHolder.bindingAdapterPosition
                )
            }
        }
    }
    fun deleteUserWithRestore(contact: Contact, position: Int) {
        if (contactsViewModel.deleteContact(contact)) {
            adapter.updateContacts(contactsViewModel.getContactsList())
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(contact.name),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.restore)) {
                    if (contactsViewModel.addContact(contact, position)) {
                        adapter.updateContacts(contactsViewModel.getContactsList())
                    }
                }.show()
        }

    }
}