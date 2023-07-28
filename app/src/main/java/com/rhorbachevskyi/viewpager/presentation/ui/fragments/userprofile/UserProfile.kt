package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var userData: UserResponse.Data


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialUser()
        setListeners()
        setUserProfile()
    }

    private fun initialUser() {
        userData = viewModel.getUser()
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

    private fun setUserProfile() {
        UserDataHolder.userData = viewModel.getUser()
        with(binding) {
            textViewName.text = userData.user.name
            textViewCareer.text = userData.user.career
            textViewHomeAddress.text = userData.user.address
        }
    }
}