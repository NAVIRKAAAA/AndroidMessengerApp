package com.rhorbachevskyi.recyclerview.ui.fragments.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recyclerview.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.ext.loadImage
import com.rhorbachevskyi.recyclerview.utils.ext.log

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentDetailViewBinding


    private val args: ProfileFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailViewBinding.inflate(inflater, container, false)
        val contact = args.contact
        log(contact.toString())
        setProfile(contact)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setProfile(contact: Contact) {
        with(binding) {
            imageViewContactProfilePhoto.loadImage(contact.photo)
            textViewName.text = contact.name
            textViewCareer.text = contact.career
        }
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