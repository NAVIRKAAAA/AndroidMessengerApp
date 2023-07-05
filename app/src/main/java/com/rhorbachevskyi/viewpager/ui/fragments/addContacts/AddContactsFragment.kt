package com.rhorbachevskyi.viewpager.ui.fragments.addContacts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.FragmentUsersBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.AddContactsAdapter
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.interfaces.UserItemClickListener
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import kotlinx.coroutines.launch


class AddContactsFragment : BaseFragment<FragmentUsersBinding>(FragmentUsersBinding::inflate) {
    private val args: AddContactsFragmentArgs by navArgs()
    private val viewModel: AddContactViewModel by viewModels()
    private val adapter: AddContactsAdapter by lazy {
        AddContactsAdapter(listener = object : UserItemClickListener {
            override fun onClickAdd(contact: Contact) {
                viewModel.addContact(contact.id, args.userData.accessToken, args.userData.user.id)
            }

            override fun onClickContact(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                TODO("Not yet implemented")
            }

            override fun onOpenNewFragment(
                contact: Contact,
                transitionPairs: Array<Pair<View, String>>
            ) {
                TODO("Not yet implemented")
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

        val layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.layoutManager = layoutManager
        binding.recyclerViewUsers.adapter = adapter
    }

    private fun setObserves() {
        viewModel.users.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        lifecycleScope.launch {
            viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUsers.Success -> {
                        binding.progressBar.invisible()
                    }

                    is ApiStateUsers.Initial -> {

                    }

                    is ApiStateUsers.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUsers.Error -> {
                        binding.progressBar.invisible()
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when(it) {
                    is ApiStateUsers.Success -> {

                    }

                    is ApiStateUsers.Initial -> {

                    }

                    is ApiStateUsers.Loading -> {
                    }

                    is ApiStateUsers.Error -> {

                    }
                }
            }
        }
    }

    private fun setListeners() {
        navigateBack()
    }

    private fun navigateBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            navController.navigateUp()
        }
    }
}