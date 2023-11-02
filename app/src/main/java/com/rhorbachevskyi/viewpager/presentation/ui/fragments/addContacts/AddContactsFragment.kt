package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.AddContactsAdapterDefault
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces.UserItemListenerDefault
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.AdapterWithPagination
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.DefaultLoadStateAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.interfaces.ClickListenerWithPagination
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder
    private val viewModel: AddContactViewModel by viewModels()
    private val adapterDefault: AddContactsAdapterDefault by lazy {
        AddContactsAdapterDefault(listener = object : UserItemListenerDefault {
            override fun onAddClick(contact: Contact) {
                viewModel.addContact(
                    UserDataHolder.userData.user.id,
                    contact,
                    UserDataHolder.userData.accessToken
                )
            }

            override fun onContactClick(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                val extras = FragmentNavigatorExtras(*transitionPairs)
                val direction =
                    AddContactsFragmentDirections.actionAddContactsFragmentToContactProfile(
                        !viewModel.supportList.contains(contact), contact
                    )
                navController.navigate(direction, extras)
            }
        })
    }

    private val adapterWithPagination: AdapterWithPagination by lazy {
        AdapterWithPagination(listener = object : ClickListenerWithPagination {
            override fun onActionClick() {
                requireContext().showSnackBar(
                    binding.root,
                    getString(R.string.No_internet_connection)
                )
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUsersList()
    }

    private fun setUsersList() {
        val hasInternet = requireContext().hasInternet()
        viewModel.getUsers(UserDataHolder.userData.accessToken, UserDataHolder.userData.user)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerViewUsers.adapter = if (hasInternet) {
            binding.loadStateView.prgBarLoadMore.invisible()
            adapterDefault
        } else {
            val footerAdapter = DefaultLoadStateAdapter()

            val adapterWithLoadState = adapterWithPagination.withLoadStateFooter(footerAdapter)
            mainLoadStateHolder = DefaultLoadStateAdapter.Holder(binding.loadStateView)
            (binding.recyclerViewUsers.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations =
                false
            adapterWithLoadState
        }
    }


    override fun setObservers() {
        val hasInternet = requireContext().hasInternet()

        if (hasInternet) setDefaultObservers() else setPaginationObservers()
    }


    private fun setDefaultObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiState.Success<*> -> {
                            progressBar.invisible()
                        }

                        is ApiState.Initial -> {
                            progressBar.invisible()
                        }

                        is ApiState.Loading -> {
                            progressBar.visible()
                        }

                        is ApiState.Error -> {
                            progressBar.invisible()
                            root.showSnackBar(requireContext(), it.error)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.states.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapterDefault.setStates(it)
            }
        }
        // list
        lifecycleScope.launch {
            viewModel.users.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                log("add users")
                adapterDefault.submitList(it)
            }
        }
    }

    private fun setPaginationObservers() {
        lifecycleScope.launch {
            viewModel.usersFlow.collectLatest { pagingData ->

                adapterWithPagination.submitData(pagingData)
            }
        }
        lifecycleScope.launch {
            adapterWithPagination.loadStateFlow.collectLatest { state ->
                mainLoadStateHolder.bind(state.refresh)
            }
        }
    }

    override fun setListeners() {
        with(binding) {
            imageViewNavigationBack.setOnClickListener { navController.navigateUp() }
            imageSearchView.setOnClickListener { viewModel.showNotification(requireContext()) }
        }
    }
}