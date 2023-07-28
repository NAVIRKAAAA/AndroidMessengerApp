package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private val viewModel: AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setListeners()
        dataValidation()
    }

    private fun setListeners() {
        with(binding) {
            buttonRegister.setOnClickListener { toExtendedScreen() }
            textViewSignIn.setOnClickListener { toSignInRegister() }
        }
    }

    private fun toExtendedScreen() {
        with(binding) {
            if (viewModel.isValidInputs(
                    textInputEditTextEmail.text.toString(),
                    textInputEditTextPassword.text.toString()
                )
            ) {
                val direction =
                    AuthFragmentDirections.actionAuthFragmentToSignUpExtendedFragment(
                        textInputEditTextEmail.text.toString(),
                        textInputEditTextPassword.text.toString(),
                        checkboxRemember.isChecked
                    )
                navController.navigate(direction)
            }
        }
    }

    private fun toSignInRegister() {
        navController.navigateUp()
    }

    private fun dataValidation() {
        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, _, _, _ ->
                textViewInvalidEmail.visibleIf(viewModel.isNotValidEmail(text.toString()))
            }
            textInputEditTextPassword.doOnTextChanged { text, _, _, _ ->
                textViewInvalidPassword.visibleIf(viewModel.isNotValidPassword(text.toString()))
            }
        }
    }
}
