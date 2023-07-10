package com.rhorbachevskyi.viewpager.ui.fragments.contact

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.RecyclerViewAdapter
import com.rhorbachevskyi.viewpager.ui.fragments.contact.adapter.interfaces.ContactItemClickListener
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentContactsBinding
import com.rhorbachevskyi.viewpager.domain.utils.NetworkImplementation
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import com.rhorbachevskyi.viewpager.utils.ext.visibleIf
import kotlinx.coroutines.launch

class ContactsFragment : BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate) {

    private val args: ContactsFragmentArgs by navArgs()
    private val viewModel: ContactsViewModel by viewModels()
    private lateinit var searchViewText: String
    private val adapter: RecyclerViewAdapter by lazy {
        RecyclerViewAdapter(listener = object : ContactItemClickListener {
            override fun onClickDelete(contact: Contact) {
                deleteUserWithRestore(contact)
            }

            override fun onClickContact(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                if (adapter.isMultiselectMode) {
                    if (viewModel.selectContacts.value?.contains(contact) == false) {
                        viewModel.addSelectContact(contact)
                    } else {
                        viewModel.deleteSelectContact(contact)
                    }
                    if (viewModel.selectContacts.value?.isEmpty() == true) {
                        viewModel.changeMultiselectMode()
                    }
                } else {
                    val extras = FragmentNavigatorExtras(*transitionPairs)
                    val direction =
                        ViewPagerFragmentDirections.actionViewPagerFragmentToContactProfile(
                            false, UserWithTokens(
                                args.userData.user,
                                args.userData.accessToken,
                                args.userData.refreshToken
                            ), contact
                        )
                    closeSearchView()
                    navController.navigate(direction, extras)
                }
            }

            override fun onLongClick(contact: Contact) {

                viewModel.changeMultiselectMode()
                if (viewModel.isMultiselect.value == true) {
                    viewModel.addSelectContact(contact)
                }

            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialRecyclerview()
        setClickListener()
        setObservers()
    }

    private fun initialRecyclerview() {
        NetworkImplementation.deleteStates()
        viewModel.initialContactList(args.userData.user.id, args.userData.accessToken)
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewContacts.layoutManager = layoutManager
        binding.recyclerViewContacts.adapter = adapter
        ItemTouchHelper(setTouchCallBackListener()).attachToRecyclerView(binding.recyclerViewContacts)
    }

    private fun setClickListener() {
        addContacts()
        navigationBack()
        deleteSelectedContacts()
        searchView()
    }

    private fun setObservers() {
        with(binding) {
            viewModel.contactList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            viewModel.isMultiselect.observe(viewLifecycleOwner) {
                recyclerViewContacts.adapter = adapter
                adapter.isMultiselectMode = it
                textViewAddContacts.visibility = if (it) View.GONE else View.VISIBLE
                imageViewDeleteSelectMode.visibility = if (it) View.VISIBLE else View.GONE
            }
            lifecycleScope.launch {
                viewModel.isSelectItem.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    adapter.setMultiselectData(it)
                }
            }
            lifecycleScope.launch {
                viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUsers.Success -> {
                            progressBar.invisible()
                        }

                        is ApiStateUsers.Error -> {
                            root.showErrorSnackBar(requireContext(), it.error)
                        }

                        is ApiStateUsers.Loading -> {
                            progressBar.visible()
                        }

                        is ApiStateUsers.Initial -> {

                        }
                    }
                }
            }
        }
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
            val size = viewModel.selectContacts.value?.size
            viewModel.deleteSelectList(args.userData.user.id, args.userData.accessToken)
            binding.root.showErrorSnackBar(
                requireContext(),
                if (size!! > 1) R.string.contacts_removed else R.string.contact_removed
            )
            viewModel.changeMultiselectMode()
        }
    }

    private fun addContacts() {

        binding.textViewAddContacts.setOnClickListener {

            val direction =
                ViewPagerFragmentDirections.actionViewPagerFragmentToAddContactsFragment(
                    UserWithTokens(
                        args.userData.user,
                        args.userData.accessToken,
                        args.userData.refreshToken
                    )
                )
            closeSearchView()
            navController.navigate(direction)

        }
    }

    private fun searchView() {
        with(binding) {
            imageSearchView.setOnCloseListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                textViewContacts.visible()
                imageViewNavigationBack.visible()
                false
            }
            imageSearchView.setOnSearchClickListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                textViewContacts.invisible()
                imageViewNavigationBack.invisible()
            }
            imageSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchViewText = newText.toString()
                    updateSearchView()
                    return false
                }
            })
        }
    }

    private fun closeSearchView() {
        with(binding) {
            imageSearchView.setQuery("", false)
            imageSearchView.isIconified = true
        }
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
                return ItemTouchHelper.LEFT
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteUserWithRestore(
                    viewModel.contactList.value?.getOrNull(viewHolder.bindingAdapterPosition)!!
                )
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                return viewModel.isMultiselect.value == false
            }
        }
    }

    fun deleteUserWithRestore(contact: Contact) {

        val position = getPosition(contact)
        if (viewModel.deleteContactFromList(
                args.userData.user.id,
                args.userData.accessToken,
                contact.id
            )
        ) {
            updateSearchView()
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(contact.name),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.restore)) {
                    if (viewModel.addContactToList(
                            args.userData.user.id,
                            contact,
                            args.userData.accessToken,
                            position
                        )
                    ) {
                        updateSearchView()
                    }
                }.show()
        }
    }

    private fun updateSearchView() {
        with(binding) {
            textViewNoResultFound.visibleIf(viewModel.updateContactList(searchViewText) == 0)
            textViewMoreContacts.visibleIf(viewModel.updateContactList(searchViewText) == 0)
            if (searchViewText.isEmpty()) initialRecyclerview()
        }
    }

    private fun getPosition(currentContact: Contact): Int {
        val contactList = viewModel.contactList.value ?: emptyList()
        return contactList.indexOfFirst { it == currentContact }
    }
}