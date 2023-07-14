package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
@AndroidEntryPoint
class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val args: UserProfileArgs by navArgs()
    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var user: UserData
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUser()
        setListeners()
        setObserver()
    }

    private fun initialUser() {
        viewModel.requestGetUser(args.userData.user.id, args.userData.accessToken)
        user = args.userData.user
    }

    private fun setListeners() {
        viewContact()
        logout()
        editProfile()
    }

    private fun viewContact() {
        binding.buttonViewContacts.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(1)
        }
    }
    private fun logout() {
        binding.textViewLogout.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_EMAIL)
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_PASSWORD)
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_REMEMBER_ME)
            }
            val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToSignInFragment()
            navController.navigate(direction)
        }
    }

    private fun editProfile() {
        binding.buttonMessageTop.setOnClickListener {
            val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToEditProfile(
                UserWithTokens(user, args.userData.accessToken, args.userData.refreshToken)
            )
            navController.navigate(direction)
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.getUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUser.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }

                    ApiStateUser.Initial -> {

                    }

                    ApiStateUser.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUser.Success -> {
                        with(binding) {
                            textViewCareer.visible()
                            textViewHomeAddress.visible()
                            progressBar.gone()
                        }
                        user = it.userData.user
                        setUserProfile()
                    }
                }
            }
        }
    }

    private fun setUserProfile() {
        with(binding) {
            textViewName.text = user.name ?: ""
            textViewCareer.text = user.career ?: ""
            textViewHomeAddress.text = user.address ?: ""
        }
    }
}