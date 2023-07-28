package com.rhorbachevskyi.viewpager.presentation.ui.fragments.contactprofile

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Contact
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentDetailViewBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.checkForInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactProfile : BaseFragment<FragmentDetailViewBinding>(FragmentDetailViewBinding::inflate) {

    private val args: ContactProfileArgs by navArgs()
    private val viewModel: ContactProfileViewModel by viewModels()
    private val userData: UserResponse.Data by lazy {
        viewModel.requestGetUser()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserver()
        setProfile(args.contact)
        setSharedElementsTransition(args.contact)
    }

    private fun setListeners() {
        with(binding) {
            imageViewNavigationBack.setOnClickListener { navigationBack() }
            buttonMessage.setOnClickListener { addToContacts() }
        }
    }


    private fun navigationBack() {
        navController.navigateUp()
    }

    private fun addToContacts() {
        if (args.isNewUser) {
            viewModel.addContact(
                userData.user.id,
                args.contact,
                userData.accessToken,
                requireContext().checkForInternet()
            )
        }
    }

    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.usersState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Error -> {
                            progressBar.invisible()
                            root.showErrorSnackBar(requireContext(), it.error)
                            viewModel.changeState()
                        }

                        ApiStateUser.Initial -> Unit

                        ApiStateUser.Loading -> {
                            progressBar.visible()
                        }

                        is ApiStateUser.Success<*> -> {
                            progressBar.gone()
                            buttonMessageTop.gone()
                            buttonMessage.text = getString(R.string.message)
                        }
                    }
                }
            }
        }
    }

    private fun setProfile(contact: Contact) {
        with(binding) {
            if (args.isNewUser) {
                buttonMessageTop.visible()
                buttonMessage.text = getString(R.string.add_to_my_contacts)
            }
            textViewName.text = contact.name
            textViewCareer.text = contact.career
            textViewHomeAddress.text = contact.address
            imageViewContactProfilePhoto.loadImage(contact.photo)
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
}