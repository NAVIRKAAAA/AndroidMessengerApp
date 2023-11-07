package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.AddContactsAdapterDefault
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapters.interfaces.UserItemClickListenerImpl
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.AdapterWithPagination
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.DefaultLoadStateAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.pagination.adapter.interfaces.ClickListenerWithPagination
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
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
        AddContactsAdapterDefault(
            UserItemClickListenerImpl(
                viewModel = viewModel,
                navController = navController
            )
        )
    }

    private val adapterWithPagination: AdapterWithPagination by lazy {
        AdapterWithPagination(listener = object : ClickListenerWithPagination {
            override fun onActionClick() {
                requireContext().showSnackBar(
                    binding.root,
                    "not has internet hahaha"
                )
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListenersInside()
        setObserversInside()
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


    private fun setObserversInside() {
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
                            requireContext().showSnackBar(root, it.error)
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

    private fun setListenersInside() {
        with(binding) {
            imageViewNavigationBack.setOnClickListener { navController.navigateUp() }
            imageSearchView.setOnClickListener { viewModel.showNotification(requireContext()) }
        }
    }
}