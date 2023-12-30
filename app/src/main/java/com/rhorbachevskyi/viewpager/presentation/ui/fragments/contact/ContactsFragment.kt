package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentContactsBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.ContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.adapter.interfaces.ContactItemClickListenerImpl
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.AdapterWithPagination
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.DefaultLoadStateAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.interfaces.ClickListenerWithPagination
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.setupSwipeToDelete
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsFragment : BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate) {
    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder
    private val viewModel: ContactsViewModel by viewModels()
    private val adapterDefault: ContactsAdapter by lazy {
        ContactsAdapter(
            ContactItemClickListenerImpl(
                viewModel = viewModel,
                navController = navController,
                deleteUserWithRestore = { deleteUserWithRestore(contact = it) })
        )
    }

    private val adapterWithPagination: AdapterWithPagination by lazy {
        AdapterWithPagination(listener = object : ClickListenerWithPagination {
            override fun onActionClick() {
                showSnackBar("not has internet hahaha")
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setContactList()
    }

    private fun setContactList() {

        val hasInternet = requireContext().hasInternet()
        viewModel.getContactList(
            UserDataHolder.userData.user,
            UserDataHolder.userData.accessToken,
            hasInternet
        )
        viewModel.deleteStates()

        with(binding) {
            recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())

            recyclerViewContacts.adapter = if (hasInternet) {
                recyclerViewContacts.setupSwipeToDelete(
                    deleteFunction = { deleteUserWithRestore(viewModel.contacts.value[it]) },
                    isSwipeEnabled = { !viewModel.isMultiselect.value && requireContext().hasInternet() }
                )
                loadStateView.prgBarLoadMore.invisible()
                adapterDefault
            } else {
                val footerAdapter = DefaultLoadStateAdapter()

                val adapterWithLoadState = adapterWithPagination.withLoadStateFooter(footerAdapter)
                mainLoadStateHolder = DefaultLoadStateAdapter.Holder(loadStateView)
                (recyclerViewContacts.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations =
                    false

                adapterWithLoadState
            }
        }
    }

    override fun setListeners() {
        with(binding) {
            textViewAddContacts.setOnClickListener { toAddContactsScreen() }
            imageViewDeleteSelectMode.setOnClickListener { deleteSelectedContacts(UserDataHolder.userData) }
            imageSearchView.setOnClickListener { viewModel.showNotification() }
        }
        navigationBack()
    }

    private fun toAddContactsScreen() {
        val direction =
            ViewPagerFragmentDirections.actionViewPagerFragmentToAddContactsFragment()
        navController.navigate(direction)
    }

    private fun deleteSelectedContacts(userData: UserResponse.Data) {
        val selectContactsSize = viewModel.selectContacts.value.size
        val snackBarText =
            if (selectContactsSize > 1) R.string.contacts_removed else R.string.contact_removed
        if (viewModel.deleteSelectList(
                userData.user.id,
                userData.accessToken,
                requireContext().hasInternet()
            )
        ) {
            binding.root.showSnackBar(requireContext(), snackBarText)
            viewModel.changeMultiselectMode()
        }
    }

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(ViewPagerAdapter.Fragments.USER_PROFILE.ordinal)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (requireParentFragment() as? ViewPagerFragment)?.openFragment(ViewPagerAdapter.Fragments.USER_PROFILE.ordinal)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun setObservers() {
        if (requireContext().hasInternet()) setDefaultObservers() else setPaginationObservers()
    }

    private fun setDefaultObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.contacts.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    adapterDefault.submitList(it)
                }
            }
            lifecycleScope.launch {
                viewModel.isMultiselect.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    recyclerViewContacts.adapter = adapterDefault
                    textViewAddContacts.visibility = if (it) View.GONE else View.VISIBLE
                    imageViewDeleteSelectMode.visibility = if (it) View.VISIBLE else View.GONE
                }
            }

            lifecycleScope.launch {
                viewModel.selectedData.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    adapterDefault.setMultiselectData(it)
                }
            }
            lifecycleScope.launch {
                viewModel.contactsStateFlow.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        when (it) {
                            is ApiState.Success<*> -> {
                                progressBar.invisible()
                            }

                            is ApiState.Error -> {
                                showSnackBar(it.error)
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

    private fun setPaginationObservers() {
        lifecycleScope.launch {
            viewModel.contactsFlow.collectLatest { pagingData ->
                adapterWithPagination.submitData(pagingData)
            }
        }
        lifecycleScope.launch {
            adapterWithPagination.loadStateFlow.collectLatest { state ->
                mainLoadStateHolder.bind(state.refresh)
            }
        }
    }

    private fun deleteUserWithRestore(contact: Contact) {
        val position = viewModel.getContactPosition(contact)
        val userData = UserDataHolder.userData
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