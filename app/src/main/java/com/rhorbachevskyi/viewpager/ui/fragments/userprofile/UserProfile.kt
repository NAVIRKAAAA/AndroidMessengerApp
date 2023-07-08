package com.rhorbachevskyi.viewpager.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStore
import com.rhorbachevskyi.viewpager.utils.ext.gone
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val args: UserProfileArgs by navArgs()
    private val viewModel: UserProfileViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.requestGetUser(args.userData.user.id, args.userData.accessToken)
        setListeners()
        setObserver()
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.getUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when(it) {
                    is ApiState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }

                    ApiState.Initial -> {

                    }
                    ApiState.Loading -> {
                        binding.progressBar.visible()
                    }
                    is ApiState.Success -> {
                        with(binding) {
                            textViewCareer.visible()
                            textViewHomeAddress.visible()
                            progressBar.gone()
                        }
                        setUserProfile(it.userData.user)
                    }
                }
            }
        }
    }

    private fun setListeners() {
        viewContact()
        logout()
        editProfile()
    }

    private fun editProfile() {
        binding.buttonMessageTop.setOnClickListener {
            val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToEditProfile(args.userData)
            navController.navigate(direction)
        }
    }

    private fun logout() {
        binding.textViewLogout.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_EMAIL)
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_PASSWORD)
                DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_REMEMBER_ME)
            }
            val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToAuthFragment()
            navController.navigate(direction)
        }
    }

    private fun viewContact() {
        binding.buttonViewContacts.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(1)
        }
    }

    private fun setUserProfile(user: UserData) {
        binding.textViewName.text = user.name.toString()
        binding.textViewCareer.text = user.career ?: ""
        binding.textViewHomeAddress.text = user.address ?: ""
    }
}