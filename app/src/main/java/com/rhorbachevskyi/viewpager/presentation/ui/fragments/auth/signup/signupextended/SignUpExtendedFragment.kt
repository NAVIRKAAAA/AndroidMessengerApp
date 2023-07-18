package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore.saveData
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpExtendedFragment : BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) {
    private val viewModel: SignUpExtendedViewModel by viewModels()
    private val args: SignUpExtendedFragmentArgs by navArgs()

    private var photoUri: Uri? = null
    private val requestImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                photoUri = it
                binding.imageViewSignUpExtendedPhoto.loadImage(it.toString())
                binding.imageViewSignUpExtendedMockup.visibility = View.GONE
            }
        }


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
        inputMobilePhone()
    }

    private fun cancel() {
        binding.buttonCancel.setOnClickListener { navController.navigateUp() }
    }

    private fun forward() {
        with(binding) {
            buttonForward.setOnClickListener {
                if (!Validation.isValidUserName(textInputEditTextUserName.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.user_name_must_contain_at_least_3_letters)
                } else if (!Validation.isValidMobilePhone(textInputEditTextMobilePhone.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.phone_must_be_at_least_10_digits_long)
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
            val request = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            requestImageLauncher.launch(request)
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
        binding.textInputEditTextUserName.setText(parsingEmail(args.email))
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
                    is ApiStateUser.Success -> {
                        if (args.rememberMe) saveData(requireContext(), args.email, args.password)
                        viewModel.isLogout()
                        val direction =
                            SignUpExtendedFragmentDirections.actionSignUpExtendedFragmentToViewPagerFragment(
                                UserWithTokens(
                                    it.userData.user,
                                    it.userData.accessToken,
                                    it.userData.refreshToken
                                )
                            )
                        navController.navigate(direction)
                    }

                    is ApiStateUser.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUser.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                        viewModel.isLogout()
                        binding.progressBar.invisible()
                    }
                    is ApiStateUser.Initial -> Unit
                }
            }
        }
    }

}