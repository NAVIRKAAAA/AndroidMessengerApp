package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.AddContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.DefaultLoadStateAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.simpleScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class  AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    private val viewModel: AddContactViewModel by viewModels()
    private val userData: UserResponse.Data by lazy {
        viewModel.requestGetUser()
    }
    private val adapter: AddContactsAdapter by lazy {
        AddContactsAdapter(listener = object : UserItemClickListener {
            override fun onClickAdd(contact: Contact) {
                viewModel.addContact(
                    userData.user.id,
                    contact,
                    userData.accessToken,
                    requireContext().hasInternet()
                )
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
    private lateinit var mainLoadStateHolder : DefaultLoadStateAdapter.Holder

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUsersList()
        setObserves()
        setListeners()
    }

    private fun setUsersList() {
        viewModel.getUsers(userData.accessToken, userData.user, requireContext().hasInternet())

        val footerAdapter = DefaultLoadStateAdapter()

        val adapterWithLoadState = adapter.withLoadStateFooter(footerAdapter)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.adapter = adapterWithLoadState
        (binding.recyclerViewUsers.itemAnimator as? DefaultItemAnimator)?.supportsChangeAnimations = false

        mainLoadStateHolder = DefaultLoadStateAdapter.Holder(
            binding,
            binding.swipeRefreshLayout
        )
        handleScrollingToTopWhenSearching()
        handleListVisibility()
        observerUsers()
    }

    private fun observerUsers() {
        lifecycleScope.launch {
            delay(2000L)
            viewModel.usersFlow.collectLatest {
                adapter.submitData(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleScrollingToTopWhenSearching() {
        lifecycleScope.launch {
            getRefreshLoadStateFlow()
            .simpleScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.recyclerViewUsers.scrollToPosition(0)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleListVisibility() = lifecycleScope.launch {

        getRefreshLoadStateFlow()
            .simpleScan(count = 3)
            .collectLatest { (beforePrevious, previous, current) ->
                binding.recyclerViewUsers.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }


    private fun setObserves() {

       lifecycleScope.launch {
           adapter.loadStateFlow.debounce(200).collectLatest {
               mainLoadStateHolder.bind(it.refresh)
           }
       }
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
    private fun getRefreshLoadStateFlow(): Flow<LoadState> {
        return adapter.loadStateFlow
            .map { it.refresh }
    }

}