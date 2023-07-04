package com.rhorbachevskyi.viewpager.ui.fragments.auth.signupextended

import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.auth.AuthViewModel
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager.saveData
import com.rhorbachevskyi.viewpager.utils.Validation
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.log
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import kotlinx.coroutines.launch

class SignUpExtendedFragment :
    BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) {
    private val viewModel: AuthViewModel by viewModels()
    private val args: SignUpExtendedFragmentArgs by navArgs()
    private lateinit var userName: String


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setSignUpExtended()
        setObservers()
    }

    private fun setListeners() {
        cancel()
        forward()
        setPhoto()
        inputUserName()
        inputMobilePhone()
    }

    private fun cancel() {
        binding.buttonCancel.setOnClickListener { navController.navigateUp() }
    }

    private fun forward() {
        with(binding) {
            buttonForward.setOnClickListener {
                if (!Validation.isValidUserName(textInputEditTextUserName.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.invalid_username)
                } else if (!Validation.isValidMobilePhone(textInputEditTextMobilePhone.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.invalid_mobile_phone)
                } else {
                    viewModel.registerUser(
                        UserRequest(
                            args.email,
                            args.password,
                            textInputEditTextUserName.text.toString(),
                            textInputEditTextMobilePhone.text.toString()
                        )
                    )
                }
            }
        }
    }

    private fun setPhoto() {
        binding.imageViewAddPhotoSignUpExtended.setOnClickListener {
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    binding.imageViewSignUpExtendedMockup.invisible()
                    binding.imageViewSignUpExtendedPhoto.loadImage(uri.toString())
                }
            }.launch("image/*")
        }
    }

    private fun inputUserName() {
        with(binding) {
            textInputEditTextUserName.doOnTextChanged { text, _, _, _ ->
                userName = text.toString()
            }
        }
    }

    private fun inputMobilePhone() {
        binding.textInputEditTextMobilePhone.addTextChangedListener(
            PhoneNumberFormattingTextWatcher(
                Constants.MOBILE_CODE
            )
        )
    }

    private fun setSignUpExtended() {
        userName = parsingEmail(args.email)
        binding.textInputEditTextUserName.setText(userName)
    }


    private fun parsingEmail(email: String): String {
        val elements = email.split("@")[0].replace(".", " ").split(" ")
        return if (elements.size >= 2) {
            "${elements[0].replaceFirstChar { it.uppercase() }} ${elements[1].replaceFirstChar { it.titlecase() }}"
        } else {
            elements[0]
        }
    }


    private fun setObservers() {
        lifecycleScope.launch {
            viewModel.registerState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiState.Success -> {

                        if (args.rememberMe) saveData(requireContext(), parsingEmail(args.email))
                        viewModel.isLogout()
                        log(it.userData.user.toString())
                        val direction =
                            SignUpExtendedFragmentDirections.actionSignUpExtendedFragmentToViewPagerFragment(it.userData.user)
                        navController.navigate(direction)
                    }

                    is ApiState.Loading -> {

                    }

                    is ApiState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                        viewModel.isLogout()
                    }
                }
            }
        }
    }

}