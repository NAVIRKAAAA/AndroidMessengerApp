package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var userData: UserResponse.Data

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialUser()
        setUserProfile()
    }

    private fun initialUser() {
        userData = viewModel.getUser()
    }

    override fun setListeners() {
        with(binding) {
            buttonViewContacts.setOnClickListener { toContactList() }
            textViewLogout.setOnClickListener { logoutFromAccount() }
            buttonMessageTop.setOnClickListener { toEditProfileScreen() }
            root.setOnRefreshListener { swipeToRefresh() }
        }
    }

    private fun swipeToRefresh() {
        lifecycleScope.launch {
            clearData()
            delay(1000L)
            initialUser()
            setUserProfile()
            binding.root.isRefreshing = false
        }
    }

    private fun clearData() {
        with(binding) {
            textViewName.text = ""
            textViewCareer.text = ""
            textViewHomeAddress.text = ""
            imageViewProfileImage.loadImage(R.drawable.ic_user_photo)
        }
    }

    private fun toContactList() {
        (parentFragment as? ViewPagerFragment)?.openFragment(ViewPagerAdapter.Fragments.CONTACTS.ordinal)
    }

    private fun logoutFromAccount() {
        viewModel.toLogout(requireContext())
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToSignInFragment()
        navController.navigate(direction)
    }

    private fun toEditProfileScreen() {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToEditProfile()
        navController.navigate(direction)
    }

    private fun setUserProfile() {
        UserDataHolder.userData = viewModel.getUser()
        with(binding) {
            textViewName.text = userData.user.name
            textViewCareer.text = userData.user.career
            textViewHomeAddress.text = userData.user.address
            imageViewProfileImage.loadImage(userData.user.image)
        }
    }
}