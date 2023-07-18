package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.databinding.FragmentEditProfileBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile.dialog.DialogCalendar
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.interfaces.DialogCalendarListener
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.Parser
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfile : BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {
    private val viewModel: EditTextViewModel by viewModels()
    private val args: EditProfileArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObserver()
        setInputs()
        setCalendar()
    }

    private fun setListeners() {
        save()
        inputPhone()
        navigationBack()
    }

    private fun save() {
        with(binding) {
            buttonSave.setOnClickListener {
                if (Validation.isValidUserName(textInputEditTextUserName.text.toString()) && Validation.isValidMobilePhone(
                        textInputEditTextPhone.text.toString()
                    )
                ) {
                    viewModel.requestEditUser(
                        args.userData.user.id,
                        args.userData.accessToken,
                        textInputEditTextUserName.text.toString(),
                        textInputEditTextCareer.text.toString(),
                        textInputEditTextPhone.text.toString(),
                        textInputEditTextAddress.text.toString(),
                        Parser.getDataFromString(textInputEditTextDate.text.toString()),
                    )
                }
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

    private fun navigationBack() {
        binding.imageViewNavigationBack.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.editUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUser.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                        binding.progressBar.invisible()
                    }

                    is ApiStateUser.Initial -> Unit

                    ApiStateUser.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUser.Success -> {
                        navController.navigateUp()
                    }
                }
            }
        }
    }

    private fun setInputs() {
        inputsErrors()
        with(binding) {
            imageViewSignUpExtendedMockup.invisible()
            imageViewSignUpExtendedPhoto.loadImage(args.userData.user.image)
            textInputEditTextUserName.setText(args.userData.user.name ?: "")
            textInputEditTextCareer.setText(args.userData.user.career ?: "")
            textInputEditTextPhone.setText(args.userData.user.phone ?: "")
            textInputEditTextAddress.setText(args.userData.user.address ?: "")
            textInputEditTextDate.setText(args.userData.user.birthday?.let {
                Parser.getStringFromData(
                    it.toString()
                )
            } ?: "")
        }
    }

    private fun inputsErrors() {
        with(binding) {
            textInputEditTextUserName.doOnTextChanged { text, _, _, _ ->
                textViewInvalidUserName.visibleIf(
                    !Validation.isValidUserName(text.toString())
                )
            }
            textInputEditTextPhone.doOnTextChanged { text, _, _, _ ->
                textViewInvalidPhone.visibleIf(!Validation.isValidMobilePhone(text.toString()))
            }
        }
    }
    private fun setCalendar() {
        with(binding) {
            textInputEditTextDate.setOnClickListener {
                val dialog = DialogCalendar()
                dialog.setListener(listener = object : DialogCalendarListener {
                    override fun onDateSelected(date: String) {
                        textInputEditTextDate.setText(date)
                    }
                })
                dialog.show(parentFragmentManager, Constants.DIALOG_TAG)
            }
        }
    }
}