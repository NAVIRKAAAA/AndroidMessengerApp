package com.rhorbachevskyi.viewpager.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val args: UserProfileArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserProfile()
        setListeners()
    }

    private fun setListeners() {
        viewContact()
        logout()
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

    private fun setUserProfile() {
        binding.textViewName.text = args.userData.user.name.toString()
        binding.textViewCareer.text = args.userData.user.career?: ""
        binding.textViewHomeAddress.text = args.userData.user.address ?: ""
    }
}