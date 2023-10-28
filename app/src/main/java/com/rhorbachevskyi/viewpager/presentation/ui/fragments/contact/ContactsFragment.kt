package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentContactsBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.ContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.interfaces.ContactItemClickListener
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.setupSwipeToDelete
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsFragment : BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate) {
    private val viewModel: ContactsViewModel by viewModels()
    private val userData: UserResponse.Data by lazy {
        viewModel.requestGetUser()
    }
    private val adapter: ContactsAdapter by lazy {
        ContactsAdapter(listener = object : ContactItemClickListener {

            override fun onContactClick(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                if (viewModel.isMultiselect.value) {
                    if (!viewModel.selectContacts.value.contains(contact)) {
                        viewModel.addSelectContact(contact)
                    } else {
                        viewModel.deleteSelectContact(contact)
                    }
                    if (viewModel.selectContacts.value.isEmpty()) {
                        viewModel.changeMultiselectMode()
                    }
                } else {
                    if (!viewModel.contactList.value.contains(contact)) return
                    val extras = FragmentNavigatorExtras(*transitionPairs)
                    val direction =
                        ViewPagerFragmentDirections.actionViewPagerFragmentToContactProfile(
                            false,
                            contact
                        )
                    navController.navigate(direction, extras)
                }
            }

            override fun onLongClick(contact: Contact) {
                viewModel.changeMultiselectMode()
                if (viewModel.isMultiselect.value) {
                    viewModel.addSelectContact(contact)
                }
            }

            override fun onDeleteClick(contact: Contact) {
                deleteUserWithRestore(contact)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialRecyclerview()
        setListener()
        setObservers()
    }

    private fun initialRecyclerview() {
        viewModel.deleteStates()
        viewModel.initialContactList(
            userData.user.id,
            userData.accessToken,
            requireContext().hasInternet()
        )
        with(binding) {
            recyclerViewContacts.layoutManager = LinearLayoutManager(context)
            recyclerViewContacts.adapter = adapter
            recyclerViewContacts.setupSwipeToDelete(
                deleteFunction = {
                    deleteUserWithRestore(viewModel.contactList.value.getOrNull(it)!!)
                },
                isSwipeEnabled = { !viewModel.isMultiselect.value && requireContext().hasInternet() }
            )
        }
    }

    private fun setListener() {
        with(binding) {
            textViewAddContacts.setOnClickListener { toAddContactsScreen() }
            imageViewDeleteSelectMode.setOnClickListener { deleteSelectedContacts() }
            imageSearchView.setOnClickListener { toSearchViewScreen() }
        }
        navigationBack()
    }

    private fun toAddContactsScreen() {
        val direction =
            ViewPagerFragmentDirections.actionViewPagerFragmentToAddContactsFragment()
        navController.navigate(direction)
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
        val size = viewModel.selectContacts.value.size
        if (viewModel.deleteSelectList(
                userData.user.id,
                userData.accessToken,
                requireContext().hasInternet()
            )
        ) {
            binding.root.showErrorSnackBar(
                requireContext(),
                if (size > 1) R.string.contacts_removed else R.string.contact_removed
            )
            viewModel.changeMultiselectMode()
        }
    }

    private fun toSearchViewScreen() {
        viewModel.showNotification(requireContext())
    }

    private fun setObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.contactList.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    adapter.submitList(it)
                }
            }
            lifecycleScope.launch {
                viewModel.isMultiselect.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    recyclerViewContacts.adapter = adapter
                    textViewAddContacts.visibility = if (it) View.GONE else View.VISIBLE
                    imageViewDeleteSelectMode.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            lifecycleScope.launch {
                viewModel.isSelectItem.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    adapter.setMultiselectData(it)
                }
            }
            lifecycleScope.launch {
                viewModel.usersStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiState.Success<*> -> {
                            progressBar.invisible()
                        }

                        is ApiState.Error -> {
                            root.showErrorSnackBar(requireContext(), it.error)
                            viewModel.changeState()
                        }

                        is ApiState.Loading -> {
                            progressBar.visible()
                        }

                        is ApiState.Initial -> Unit
                    }
                }
            }
        }
    }


    fun deleteUserWithRestore(contact: Contact) {
        val position = viewModel.contactList.value.indexOfFirst { it == contact }
        if (viewModel.deleteContactFromList(
                userData.user.id,
                userData.accessToken,
                contact,
                requireContext().hasInternet()
            )
        ) {
            Snackbar.make(
                binding.recyclerViewContacts,
                getString(R.string.s_has_been_removed).format(contact.name),
                Snackbar.LENGTH_LONG
            ).setAction(getString(R.string.restore)) {
                viewModel.addContactToList(
                    userData.user.id,
                    contact,
                    userData.accessToken,
                    position
                )
            }.show()
        }
    }
}