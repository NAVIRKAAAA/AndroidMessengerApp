package com.rhorbachevskyi.viewpager.ui.fragments.auth.signupextended

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.auth.AuthViewModel
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpExtendedFragment :
    BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) { // todo: user image?
    private lateinit var viewModel: AuthViewModel
    private val args: SignUpExtendedFragmentArgs by navArgs()
    private lateinit var userName: String
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.imageViewSignUpExtendedMockup.invisible()
                binding.imageViewSignUpExtendedPhoto.loadImage(uri.toString())
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
        inputUserName()
        inputMobilePhone()
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


    private fun inputMobilePhone() {
        binding.textInputEditTextMobilePhone.addTextChangedListener(
            PhoneNumberFormattingTextWatcher(
                "US"
            )
        )
    }

    private fun setPhoto() {
        binding.imageViewAddPhotoSignUpExtended.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private fun cancel() {
        binding.buttonCancel.setOnClickListener { navController.navigateUp() }
    }

    private fun inputUserName() {
        with(binding) {
            textInputEditTextUserName.doOnTextChanged { text, _, _, _ ->
                userName = text.toString()
            }
        }
    }

    private fun forward() {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.buttonForward.setOnClickListener {
            viewModel.registerUser(
                UserRequest(
                    args.email,
                    args.password
                )
            )
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
}