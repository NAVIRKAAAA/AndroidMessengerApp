package com.rhorbachevskyi.recyclerview.ui.fragments.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.recyclerview.databinding.FragmentDetailViewBinding

class ProfileFragment : Fragment() {
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
//        with(binding) {
//            imageViewContactProfilePhoto.loadImage(user.photo)
//            textViewName.text = user.name
//            textViewCareer.text = user.career
//        }
    }

    private fun setListeners() {
        setNavigationBack()
    }

    private fun setNavigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}