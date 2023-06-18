package com.rhorbachevskyi.recyclerview.ui.fragments.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview.R
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.recyclerview.repository.ContactItemClickListener
import com.rhorbachevskyi.recyclerview.ui.fragments.dialog.DialogFragment
import com.example.recyclerview.databinding.FragmentContactsBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.Constants
import com.rhorbachevskyi.recyclerview.utils.ext.animateVisibility
import com.google.android.material.snackbar.Snackbar

class ContactsFragment : Fragment(), ContactItemClickListener {
    private lateinit var binding: FragmentContactsBinding
    private val navController by lazy { findNavController() }
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
        setNavigationUpListeners()
    }

    private fun initialRecyclerview() {
        setTouchRecycleItemListener()
        userViewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        val layoutManager = LinearLayoutManager(context)
        adapter.setUserItemClickListener(this)
        binding.recyclerViewContacts.layoutManager = layoutManager
        binding.recyclerViewContacts.adapter = adapter
        adapter.updateUsers(userViewModel.getUserList())
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


    private fun setNavigationUpListeners() {
        binding.imageViewNavigationUp.viewTreeObserver.addOnScrollChangedListener {
            checkForDisplayUpNavigationButton()
        }
        binding.imageViewNavigationUp.setOnClickListener {
            binding.recyclerViewContacts.smoothScrollToPosition(0)
        }
    }

    private fun checkForDisplayUpNavigationButton() {
        val visibleItemCount = binding.recyclerViewContacts.childCount
        val layoutManager = binding.recyclerViewContacts.layoutManager as LinearLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        binding.imageViewNavigationUp.animateVisibility(
            if (lastVisibleItemPosition >= visibleItemCount) View.VISIBLE else View.GONE
        )
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
                    userViewModel.getUserList()[viewHolder.adapterPosition],
                    viewHolder.adapterPosition
                )
            }
        }
    }

    override fun onUserDelete(user: Contact, position: Int) {
        deleteUserWithRestore(user, position)
    }

    override fun onOpenNewFragment(user: Contact) {
        val direction = ContactsFragmentDirections.actionContactsFragmentToProfileFragment(user)
        findNavController().navigate(direction)
    }

    fun deleteUserWithRestore(user: Contact, position: Int) {
        if (userViewModel.deleteUser(user)) {
            adapter.notifyItemRemoved(position)
            adapter.updateUsers(userViewModel.getUserList())
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(user.name),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.restore)) {
                    if (userViewModel.addUser(user, position)) {
                        adapter.notifyItemInserted(position)
                        adapter.updateUsers(userViewModel.getUserList())
                    }
                }.show()
        }
    }
}