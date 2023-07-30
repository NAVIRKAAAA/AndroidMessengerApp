package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.AddContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.presentation.utils.ext.checkForInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

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
                    requireContext().checkForInternet()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialRecyclerview()
        setObserves()
        setListeners()
    }

    private fun initialRecyclerview() {
        viewModel.getAllUsers(userData.accessToken, userData.user, requireContext().checkForInternet())
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.adapter = adapter
    }


    private fun setObserves() {
        lifecycleScope.launch {
            viewModel.users.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapter.submitList(it)
            }
        }
        with(binding) {
            lifecycleScope.launch {
                viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Success<*> -> {
                            progressBar.invisible()
                        }

                        is ApiStateUser.Initial -> {
                            progressBar.invisible()
                        }

                        is ApiStateUser.Loading -> {
                            progressBar.visible()
                        }

                        is ApiStateUser.Error -> {
                            progressBar.invisible()
                            root.showErrorSnackBar(requireContext(), it.error)
                            viewModel.changeState()
                        }
                    }
                }
            }
        }
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
}