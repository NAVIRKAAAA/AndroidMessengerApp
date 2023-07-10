package com.rhorbachevskyi.viewpager.ui.fragments.userprofile.editprofile

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.databinding.FragmentEditProfileBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.dialog.DialogCalendar
import com.rhorbachevskyi.viewpager.ui.fragments.userprofile.interfaces.DialogCalendarListener
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.Parser
import com.rhorbachevskyi.viewpager.utils.Validation
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
import com.rhorbachevskyi.viewpager.utils.ext.visibleIf
import kotlinx.coroutines.launch

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
                        UserData(
                            args.userData.user.id,
                            textInputEditTextUserName.text.toString(),
                            args.userData.user.email,
                            textInputEditTextPhone.text.toString(),
                            textInputEditTextAddress.text.toString(),
                            textInputEditTextCareer.text.toString(),
                            Parser.getDataFromString(textInputEditTextDate.text.toString()),
                            args.userData.user.facebook.toString(),
                            args.userData.user.instagram.toString(),
                            args.userData.user.twitter.toString(),
                            args.userData.user.linkedin.toString()
                        ), args.userData.accessToken
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

    private fun setInputs() {
        inputsErrors()
        with(binding) {
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
                dialog.setListeners(listener = object : DialogCalendarListener {
                    override fun onDateSelected(date: String) {
                        textInputEditTextDate.setText(date)
                    }

                })
                dialog.show(parentFragmentManager, Constants.DIALOG_TAG)
            }
        }
    }


}