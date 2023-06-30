package com.rhorbachevskyi.viewpager.ui.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpBinding
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.utils.Validation
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.visibleIf

class AuthFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        dataValidation()
    }
    private fun setListeners() {
        with(binding) {
            buttonRegister.setOnClickListener {
                if (Validation().isValidEmail(textInputEditTextEmail.text.toString()) &&
                    Validation().isValidPassword(textInputEditTextPassword.text.toString())
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
    private fun dataValidation() {
        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, _, _, _ ->
                textViewInvalidEmail.visibleIf(
                    !Validation().isValidEmail(text.toString()) && !text.isNullOrEmpty()
                )
            }
            textInputEditTextPassword.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrEmpty()) {
                    textViewInvalidPassword.visibility =
                        if (Validation().isValidPassword(text.toString())) View.INVISIBLE else View.VISIBLE
                } else {
                    textViewInvalidPassword.invisible()
                }
            }
        }
    }
}
