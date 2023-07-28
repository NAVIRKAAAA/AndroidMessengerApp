package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.databinding.FragmentEditProfileBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile.dialog.DialogCalendar
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.interfaces.DialogCalendarListener
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.Parser
import com.rhorbachevskyi.viewpager.presentation.utils.ext.checkForInternet
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
    private val userData: UserResponse.Data by lazy {
        viewModel.requestGetUser()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObserver()
        setInputs()
        setCalendar()
    }

    private fun setListeners() {
        with(binding) {
            buttonSave.setOnClickListener { saveUserData() }
            imageViewNavigationBack.setOnClickListener { toUserProfile() }
        }
    }

    private fun saveUserData() {
        with(binding) {
            if (viewModel.isValidInputs(
                    textInputEditTextUserName.text.toString(),
                    textInputEditTextPhone.text.toString()
                )
            ) {
                viewModel.requestEditUser(
                    userData.user.id,
                    userData.accessToken,
                    textInputEditTextUserName.text.toString(),
                    textInputEditTextCareer.text.toString(),
                    textInputEditTextPhone.text.toString(),
                    textInputEditTextAddress.text.toString(),
                    Parser.getDataFromString(textInputEditTextDate.text.toString()),
                    requireContext().checkForInternet(),
                    userData.refreshToken
                )
            }
        }
    }

    private fun toUserProfile() {
        navController.navigateUp()
    }

    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.editUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Success<*> -> {
                            navController.navigateUp()
                        }

                        is ApiStateUser.Error -> {
                            root.showErrorSnackBar(requireContext(), it.error)
                            progressBar.invisible()
                        }

                        ApiStateUser.Initial -> {}

                        ApiStateUser.Loading -> {
                            progressBar.visible()
                        }
                    }
                }
            }
        }
    }

    private fun setInputs() {
        inputsErrors()
        with(binding) {
            imageViewSignUpExtendedMockup.invisible()
            imageViewSignUpExtendedPhoto.loadImage(userData.user.image)
            textInputEditTextUserName.setText(userData.user.name ?: "")
            textInputEditTextCareer.setText(userData.user.career ?: "")
            textInputEditTextPhone.setText(userData.user.phone ?: "")
            textInputEditTextAddress.setText(userData.user.address ?: "")
            textInputEditTextDate.setText(userData.user.birthday?.let {
                Parser.getStringFromData(it.toString())
            } ?: "")
        }
    }

    private fun inputsErrors() {
        with(binding) {
            textInputEditTextUserName.doOnTextChanged { text, _, _, _ ->
                textViewInvalidUserName.visibleIf(viewModel.isNotValidUserName(text.toString())
                )
            }
            textInputEditTextPhone.doOnTextChanged { text, _, _, _ ->
                textViewInvalidPhone.visibleIf(viewModel.isNotValidMobilePhone(text.toString()))
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