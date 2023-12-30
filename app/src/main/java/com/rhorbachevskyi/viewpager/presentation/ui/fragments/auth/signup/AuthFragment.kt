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

        dataValidation()
    }

    override fun setListeners() {
        with(binding) {
            buttonRegister.setOnClickListener { toExtendedScreen() }
            textViewSignIn.setOnClickListener { navController.navigateUp() }
        }
    }

    private fun toExtendedScreen() {
        with(binding) {
            val email = textInputEditTextEmail.text.toString()
            val password = textInputEditTextPassword.text.toString()
            if (viewModel.isValidInputs(email, password)) {
                val direction =
                    AuthFragmentDirections.actionAuthFragmentToSignUpExtendedFragment(
                        email,
                        password,
                        checkboxRemember.isChecked
                    )
                navController.navigate(direction)
            }
        }
    }

    private fun dataValidation() {
        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, _, _, _ ->
                textViewInvalidEmail.visibleIf(!viewModel.isValidEmail(text.toString()))
            }
            textInputEditTextPassword.doOnTextChanged { text, _, _, _ ->
                textViewInvalidPassword.visibleIf(!viewModel.isValidPassword(text.toString()))
            }
        }
    }
}
