package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.paging.DefaultLoadStateAdapter
import com.rhorbachevskyi.viewpager.paging.TryAgainAction
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.AddContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    private lateinit var mainLoadStateHolder: DefaultLoadStateAdapter.Holder
    private val viewModel: AddContactViewModel by viewModels()
    private val userData: UserResponse.Data by lazy {
        viewModel.requestGetUser()
    }
    private val adapter: AddContactsAdapter by lazy {
        AddContactsAdapter(listener = object : UserItemClickListener {
            override fun onClickAdd(contact: Contact) {
//                viewModel.addContact(
//                    userData.user.id,
//                    contact,
//                    userData.accessToken,
//                    requireContext().hasInternet()
//                )
            }

            override fun onClickContact(
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        log("token fr: ${viewModel.requestGetUser()}")
        setUsersList()
        setObserves()
        setListeners()
    }

    private fun setUsersList() {
//        viewModel.getUsers(userData.accessToken, userData.user, requireContext().hasInternet())
//        with(binding) {
//            recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
//            recyclerViewUsers.adapter = adapter
//        }
//        lifecycleScope.launch {
//            adapter.loadStateFlow.collectLatest {
//                val state = it.refresh
//                binding.progressBar.visibleIf(state is LoadState.Loading)
//            }
//        }
//        binding.recyclerViewUsers.adapter = adapter.withLoadStateFooter(
//            LoadMoreAdapter { adapter.retry() }
//        )

        val tryAgainAction: TryAgainAction = { adapter.retry() }

        val footerAdapter = DefaultLoadStateAdapter(tryAgainAction)

        // combined adapter which shows both the list of users + footer indicator when loading pages
        val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapterWithLoadState
        (binding.recyclerViewUsers.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
            binding.loadStateView,
            binding.swipeRefreshLayout,
            tryAgainAction
        )

        observeUsers(adapter)
        observeLoadState(adapter)

        handleScrollingToTopWhenSearching(adapter)
        handleListVisibility(adapter)
    }


    private fun setObserves() {


//        with(binding) {
//            lifecycleScope.launch {
//                viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
//                    when (it) {
//                        is ApiState.Success<*> -> {
//                            progressBar.invisible()
//                        }
//
//                        is ApiState.Initial -> {
//                            progressBar.invisible()
//                        }
//
//                        is ApiState.Loading -> {
//                            progressBar.visible()
//                        }
//
//                        is ApiState.Error -> {
//                            progressBar.invisible()
//                            root.showErrorSnackBar(requireContext(), it.error)
//                            viewModel.changeState()
//                        }
//                    }
//                }
//            }
//        }
        lifecycleScope.launch {
            viewModel.states.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapter.setStates(it)
            }
        }
    }

    private fun setListeners() {
        with(binding) {
            imageViewNavigationBack.setOnClickListener { navigationBack() }
            imageSearchView.setOnClickListener { searchView() }
        }
    }

    private fun navigationBack() {
        navController.navigateUp()
    }

    private fun searchView() {
        viewModel.showNotification(requireContext())
    }


    private fun setupSwipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            // viewModel.getUsers()
        }
    }

    private fun observeUsers(adapter: AddContactsAdapter) {
        lifecycleScope.launch {
            viewModel.usersList.collectLatest { pagingData ->
                adapter.submitData(pagingData.map { it.toContact() })
            }
        }
    }

    private fun observeLoadState(adapter: AddContactsAdapter) {
        // you can also use adapter.addLoadStateListener
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                // main indicator in the center of the screen
                mainLoadStateHolder.bind(state.refresh)
            }
        }
    }

    private fun handleScrollingToTopWhenSearching(adapter: AddContactsAdapter) = lifecycleScope.launch {
        // list should be scrolled to the 1st item (index = 0) if data has been reloaded:
        // (prev state = Loading, current state = NotLoading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.recyclerViewUsers.scrollToPosition(0)
                }
            }
    }

    private fun handleListVisibility(adapter: AddContactsAdapter) = lifecycleScope.launch {
        // list should be hidden if an error is displayed OR if items are being loaded after the error:
        // (current state = Error) OR (prev state = Error)
        //   OR
        // (before prev state = Error, prev state = NotLoading, current state = Loading)
        getRefreshLoadStateFlow(adapter)
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.recyclerViewUsers.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(adapter: AddContactsAdapter): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

}