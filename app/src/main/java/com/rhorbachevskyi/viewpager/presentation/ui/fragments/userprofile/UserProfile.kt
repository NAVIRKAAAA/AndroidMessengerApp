package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var userData: UserResponse.Data
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUser()
        setListeners()
        setObserver()
    }

    private fun initialUser() {
        viewModel.requestGetUser()
    }

    private fun setListeners() {
        with(binding) {
            buttonViewContacts.setOnClickListener { viewContact() }
            textViewLogout.setOnClickListener { logout() }
            buttonMessageTop.setOnClickListener { editProfile() }
        }
    }

    private fun viewContact() {
        (parentFragment as? ViewPagerFragment)?.openFragment(Constants.CONTACTS_FRAGMENT)
    }

    private fun logout() {
        viewModel.toLogout(requireContext())
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToSignInFragment()
        navController.navigate(direction)
    }

    private fun editProfile() {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToEditProfile()
        navController.navigate(direction)
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.getUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUser.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }

                    ApiStateUser.Initial -> Unit

                    ApiStateUser.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUser.Success -> {
                        with(binding) {
                            textViewCareer.visible()
                            textViewHomeAddress.visible()
                            progressBar.gone()
                        }
                        userData = it.userData
                        setUserProfile()
                    }
                }
            }
        }
    }

    private fun setUserProfile() {
        with(binding) {
            textViewName.text = userData.user.name ?: ""
            textViewCareer.text = userData.user.career ?: ""
            textViewHomeAddress.text = userData.user.address ?: ""
        }
    }
}