package com.rhorbachevskyi.viewpager.ui.fragments.contactprofile

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.loadImage

class ContactProfile : BaseFragment<FragmentDetailViewBinding>(FragmentDetailViewBinding::inflate) {

    private val args: ContactProfileArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contact = args.contact
        setProfile(contact)
        setSharedElementsTransition(contact)
        setListeners()
    }
    private fun setSharedElementsTransition(contact: Contact) {
        with(binding) {
            imageViewContactProfilePhoto.transitionName =
                Constants.TRANSITION_NAME_IMAGE + contact.id
            textViewName.transitionName = Constants.TRANSITION_NAME_NAME + contact.id
            textViewCareer.transitionName = Constants.TRANSITION_NAME_CAREER + contact.id
        }
        val animation = TransitionInflater.from(context).inflateTransition(
            R.transition.custom_move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    private fun setProfile(contact: Contact) {
        with(binding) {
            imageViewContactProfilePhoto.loadImage(contact.image)
            textViewName.text = contact.name
            textViewCareer.text = contact.career
        }
    }

    private fun setListeners() {
        setNavigationBack()
    }

    private fun setNavigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            navController.navigateUp()
        }
    }
}