package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpBinding
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.Validation
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visibleIf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        dataValidation()

    }
    private fun setListeners() {
        register()
        signIn()
    }
    private fun register() {
        with(binding) {
            buttonRegister.setOnClickListener {
                if (Validation.isValidEmail(textInputEditTextEmail.text.toString()) &&
                    Validation.isValidPassword(textInputEditTextPassword.text.toString())
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
    }
    private fun signIn() {
        binding.textViewSignIn.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun dataValidation() {
        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, _, _, _ ->
                textViewInvalidEmail.visibleIf(!Validation.isValidEmail(text.toString()) && !text.isNullOrEmpty())
            }
            textInputEditTextPassword.doOnTextChanged { text, _, _, _ ->
                textViewInvalidPassword.visibleIf(!Validation.isValidPassword(text.toString()) && !text.isNullOrEmpty())
            }
        }
    }
}
