package com.rhorbachevskyi.recyclerview.ui.fragments.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recyclerview.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.recyclerview.domain.model.User
import com.rhorbachevskyi.recyclerview.repository.UserItemClickListener
import com.rhorbachevskyi.recyclerview.ui.contract.navigator
import com.rhorbachevskyi.recyclerview.utils.ext.loadImage

class ProfileFragment(private val user: User) : Fragment() {
    private lateinit var binding: FragmentDetailViewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setProfile()
    }

    private fun setProfile() {
        with(binding) {
            imageViewContactProfilePhoto.loadImage(user.photo)
            textViewName.text = user.name
            textViewCareer.text = user.career
        }
    }

    private fun setListeners() {
        setNavigationBack()
    }

    private fun setNavigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}