package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserData
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentEditProfileBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.editprofile.dialog.DialogCalendar
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.interfaces.DialogCalendarListener
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.Parser
import com.rhorbachevskyi.viewpager.presentation.utils.ext.hasInternet
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfile : BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {
    private val viewModel: EditTextViewModel by viewModels()

    override fun setListeners() {
        with(binding) {
            buttonSave.setOnClickListener { saveUserData() }
            imageViewNavigationBack.setOnClickListener { navController.navigateUp() }
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
                    UserDataHolder.userData.user.id,
                    UserDataHolder.userData.accessToken,
                    textInputEditTextUserName.text.toString(),
                    textInputEditTextCareer.text.toString(),
                    textInputEditTextPhone.text.toString(),
                    textInputEditTextAddress.text.toString(),
                    Parser.getDataFromString(textInputEditTextDate.text.toString()),
                    requireContext().hasInternet(),
                    UserDataHolder.userData.refreshToken
                )
            }
        }
    }


    override fun setObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.editUserState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiState.Success<*> -> { navController.navigateUp() }

                        is ApiState.Error -> {
                            root.showSnackBar(requireContext(), it.error)
                            progressBar.invisible()
                        }

                        ApiState.Initial -> {
                            setCalendar()
                            setInputs(UserDataHolder.userData.user)
                        }

                        ApiState.Loading -> { progressBar.visible() }
                    }
                }
            }
        }
    }

    private fun setInputs(user: UserData) {
        inputsErrors()
        with(binding) {
            imageViewSignUpExtendedMockup.invisible()
            imageViewSignUpExtendedPhoto.loadImage(user.image)
            textInputEditTextUserName.setText(user.name ?: "")
            textInputEditTextCareer.setText(user.career ?: "")
            textInputEditTextPhone.setText(user.phone ?: "")
            textInputEditTextAddress.setText(user.address ?: "")
            textInputEditTextDate.setText(user.birthday?.let {
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