package com.rhorbachevskyi.recyclerview.ui.fragments.userProfile

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.recyclerview.R
import com.example.recyclerview.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.recyclerview.domain.model.Contact
import com.rhorbachevskyi.recyclerview.utils.Constants
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
        setProfile(contact)
        setSharedElementsTransition(contact)
        setListeners()
        return binding.root
    }

    private fun setSharedElementsTransition(contact: Contact) {
        with(binding) {
            imageViewContactProfilePhoto.transitionName =
                Constants.TRANSITION_NAME_IMAGE + contact.id
            textViewName.transitionName = Constants.TRANSITION_NAME_NAME + contact.id
            textViewCareer.transitionName = Constants.TRANSITION_NAME_CAREER + contact.id
        }
        val animation = TransitionInflater.from(context).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
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