package com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUsers
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.AddContactsAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {

    private val args: AddContactsFragmentArgs by navArgs()
    private val viewModel: AddContactViewModel by viewModels()
    private val adapter: AddContactsAdapter by lazy {
        AddContactsAdapter(listener = object : UserItemClickListener {
            override fun onClickAdd(contact: Contact) {
                viewModel.addContact(args.userData.user.id, contact, args.userData.accessToken)
            }

            override fun onClickContact(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                val extras = FragmentNavigatorExtras(*transitionPairs)
                val direction =
                    AddContactsFragmentDirections.actionAddContactsFragmentToContactProfile(
                        !viewModel.supportList.contains(contact), UserWithTokens(
                            args.userData.user,
                            args.userData.accessToken,
                            args.userData.refreshToken
                        ), contact
                    )
                closeSearchView()
                navController.navigate(direction, extras)
            }

        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllUsers(args.userData.accessToken, args.userData.user)
        initialRecyclerview()
        setObserves()
        setListeners()
    }

    private fun initialRecyclerview() {
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.adapter = adapter
    }


    private fun setObserves() {
        lifecycleScope.launch {
            viewModel.users.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUsers.Success -> {
                        binding.progressBar.invisible()
                    }
                    is ApiStateUsers.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUsers.Error -> {
                        binding.progressBar.invisible()
                        binding.root.showErrorSnackBar(requireContext(), it.error)

                    }
                    is ApiStateUsers.Initial -> Unit
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
        navigateBack()
        searchView()
    }

    private fun navigateBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun searchView() {
        with(binding) {
            imageSearchView.setOnCloseListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                textViewUsers.visible()
                imageViewNavigationBack.visible()
                false
            }
            imageSearchView.setOnSearchClickListener {
                imageSearchView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                textViewUsers.invisible()
                imageViewNavigationBack.invisible()
            }
            imageSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    textViewNoResultFound.visibleIf(viewModel.updateContactList(newText) == 0)
                    if (newText.isNullOrEmpty()) initialRecyclerview()
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
}