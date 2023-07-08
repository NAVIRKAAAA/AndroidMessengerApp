package com.rhorbachevskyi.viewpager.ui.fragments.userprofile.editprofile

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.databinding.FragmentEditProfileBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import kotlinx.coroutines.launch

class EditProfile : BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {
    private val viewModel: EditTextViewModel by viewModels()
    private val args: EditProfileArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserver()
    }

    private fun setListeners() {
        save()
        inputPhone()
    }

    private fun save() {
        with(binding) {
            buttonSave.setOnClickListener {
                viewModel.requestEditUser(
                    UserData(
                        args.userData.user.id,
                        textInputEditTextUserName.text.toString()
                            .ifEmpty { args.userData.user.name },
                        args.userData.user.email,
                        textInputEditTextPhone.text.toString().ifEmpty { args.userData.user.phone },
                        textInputEditTextAddress.text.toString()
                            .ifEmpty { args.userData.user.address },
                        textInputEditTextCareer.text.toString()
                            .ifEmpty { args.userData.user.career },
                        textInputEditTextDate.text.toString()
                            .ifEmpty { args.userData.user.birthday },
                        args.userData.user.facebook.toString(),
                        args.userData.user.instagram.toString(),
                        args.userData.user.twitter.toString(),
                        args.userData.user.linkedin.toString()
                    ), args.userData.accessToken
                )
            }
        }
    }

    private fun inputPhone() {
        binding.textInputEditTextPhone.addTextChangedListener(
            PhoneNumberFormattingTextWatcher(
                Constants.MOBILE_CODE
            )
        )
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.editUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }

                    ApiState.Initial -> {

                    }

                    ApiState.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiState.Success -> {
                        navController.navigateUp()
                    }
                }
            }
        }
    }
}