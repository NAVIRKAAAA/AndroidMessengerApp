package com.rhorbachevskyi.viewpager.ui.fragments.auth.signupextended

import android.net.Uri
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.auth.AuthViewModel
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
import com.rhorbachevskyi.viewpager.utils.Validation
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpExtendedFragment :
    BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) {
    private lateinit var viewModel: AuthViewModel
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
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        with(binding) {
            buttonForward.setOnClickListener {
                if(!Validation().isValidUserName(textInputEditTextUserName.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.invalid_username)
                } else if(!Validation().isValidMobilePhone(textInputEditTextMobilePhone.text.toString())) {
                    root.showErrorSnackBar(requireContext(), R.string.invalid_mobile_phone)
                } else {
                    viewModel.registerUser(
                        UserRequest(
                            args.email,
                            args.password
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
        userName = parsingEmail()
        binding.textInputEditTextUserName.setText(userName)
    }


    private fun parsingEmail(): String {
        val elements = args.email.split("@")[0].replace(".", " ").split(" ")
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
                    is AuthViewModel.RegisterState.Success -> {
                        if (args.rememberMe) saveData()
                        viewModel.isLogout()
                        val direction =
                            SignUpExtendedFragmentDirections.actionSignUpExtendedFragmentToViewPagerFragment(
                                userName
                            )
                        navController.navigate(direction)
                    }

                    is AuthViewModel.RegisterState.Loading -> {

                    }

                    is AuthViewModel.RegisterState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                        viewModel.isLogout()
                    }
                }
            }
        }
    }

    private fun saveData() {
        lifecycleScope.launch(Dispatchers.IO) {
            DataStoreManager.putData(
                requireContext(),
                Constants.KEY_EMAIL,
                userName
            )
            DataStoreManager.putData(
                requireContext(),
                Constants.KEY_REMEMBER_ME,
                Constants.KEY_REMEMBER_ME
            )
        }
    }
}