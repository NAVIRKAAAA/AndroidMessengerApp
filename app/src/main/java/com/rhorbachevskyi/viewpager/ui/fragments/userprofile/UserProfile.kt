package com.rhorbachevskyi.viewpager.ui.fragments.userprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.utils.ext.log

class UserProfile : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val args: UserProfileArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setUserProfile()
        setListeners()
        return binding.root
    }

    private fun setListeners() {
        viewContact()
        logout()

    }

    private fun logout() {
        binding.textViewLogout.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun viewContact() {
        binding.buttonViewContacts.setOnClickListener {
            (parentFragment as? ViewPagerFragment)?.openFragment(1)
        }
    }

    private fun setUserProfile() {
        setUsername(args.email)
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