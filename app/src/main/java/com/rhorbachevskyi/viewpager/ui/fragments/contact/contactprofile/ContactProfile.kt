package com.rhorbachevskyi.viewpager.ui.fragments.contact.contactprofile

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.addContacts.adapter.utils.ApiStateUsers
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.gone
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import kotlinx.coroutines.launch

class ContactProfile : BaseFragment<FragmentDetailViewBinding>(FragmentDetailViewBinding::inflate) {

    private val args: ContactProfileArgs by navArgs()
    private val viewModel: ContactProfileViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contact = args.contact
        setProfile(contact)
        setSharedElementsTransition(contact)
        setListeners()
        setObserver()
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when(it) {
                    is ApiStateUsers.Error -> binding.root.showErrorSnackBar(requireContext(), it.error)
                    ApiStateUsers.Initial -> {

                    }
                    ApiStateUsers.Loading -> {
                        binding.progressBar.visible()
                    }
                    is ApiStateUsers.Success -> {
                        binding.progressBar.gone()
                        binding.buttonMessageTop.gone()
                        binding.buttonMessage.text = getString(R.string.message)
                    }
                }
            }
        }
    }

    private fun setSharedElementsTransition(contact: Contact) {
        with(binding) {
            imageViewContactProfilePhoto.transitionName =
                Constants.TRANSITION_NAME_IMAGE + contact.id
            textViewName.transitionName = Constants.TRANSITION_NAME_CONTACT_NAME + contact.id
            textViewCareer.transitionName = Constants.TRANSITION_NAME_CAREER + contact.id
        }
        val animation = TransitionInflater.from(context).inflateTransition(
            R.transition.custom_move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    private fun setProfile(contact: Contact) {
        if (args.isNewUser) {
            with(binding) {
                buttonMessageTop.visible()
                buttonMessage.text = getString(R.string.add_to_my_contacts)
            }
        }
        with(binding) {
            imageViewContactProfilePhoto.loadImage(contact.photo)
            textViewName.text = contact.name
            textViewCareer.text = contact.career
            textViewHomeAddress.text = contact.address
        }
    }

    private fun setListeners() {
        navigationBack()
        addToContacts()
    }

    private fun addToContacts() {
        if(args.isNewUser) {
            binding.buttonMessage.setOnClickListener {
                viewModel.addContact(args.userData.user.id, args.contact, args.userData.accessToken)
            }
        }
    }

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            navController.navigateUp()
        }
    }
}