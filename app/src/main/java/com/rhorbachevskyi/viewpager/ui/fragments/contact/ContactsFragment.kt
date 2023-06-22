package com.rhorbachevskyi.viewpager.ui.fragments.contact

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
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.viewpager.domain.repository.ContactItemClickListener
import com.rhorbachevskyi.viewpager.ui.fragments.dialog.DialogFragment
import com.rhorbachevskyi.viewpager.domain.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentContactsBinding
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.log

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
        initialViewPager()
        setClickListener()
    }

    private fun initialViewPager() {

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
        navigationBack()
    }

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToContactProfile(contact)
        val extras = FragmentNavigatorExtras(*transitionPairs)
        findNavController().navigate(direction, extras)
    }

    override fun showImageDeleteBin() {
        binding.imageViewDeleteSelectMode.visibility = View.VISIBLE
    }

    override fun hideImageDeleteBin() {
        binding.imageViewDeleteSelectMode.visibility = View.GONE
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