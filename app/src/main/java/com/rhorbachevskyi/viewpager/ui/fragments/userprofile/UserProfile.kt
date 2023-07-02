package com.rhorbachevskyi.viewpager.ui.fragments.userprofile

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.ui.fragments.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
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
                DataStoreManager.deleteDataFromDataStore(requireContext(), Constants.KEY_EMAIL)
                DataStoreManager.deleteDataFromDataStore(
                    requireContext(),
                    Constants.KEY_REMEMBER_ME
                )
            }
            navController.navigateUp()
        }
    }

    private fun viewContact() {
        binding.buttonViewContacts.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(1)
        }
    }

    private fun setUserProfile() {
        setAddress()
        setCareer()
        setUsername(args.email)
    }

    private fun setAddress() {
        binding.textViewHomeAddress.text = ""
    }


    private fun setCareer() {
        binding.textViewCareer.text = ""
    }


    private fun setUsername(email: String) {
        val elements = email.split("@")[0].replace(".", " ").split(" ")
        binding.textViewName.text = if (elements.size >= 2) {
            "${elements[0].replaceFirstChar { it.uppercase() }} ${elements[1].replaceFirstChar { it.titlecase() }}"
        } else {
            elements[0]
        }
    }
}