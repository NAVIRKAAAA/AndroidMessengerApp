package com.rhorbachevskyi.viewpager.ui.fragments.auth

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpBinding
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.visibleIf

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater,container,false)
        setListeners()
        dataValidation()
        return binding.root
    }

    private fun isRememberMe() {
        if (sharedPreferences.getString(Constants.KEY_REMEMBER_ME, "") != "") {

        }
    }

    private fun setListeners() {
        with(binding) {
            buttonRegister.setOnClickListener {
                if (isValidEmail(textInputEditTextEmail.text.toString()) &&
                    isValidPassword(textInputEditTextPassword.text.toString())
                ) {
                    if (checkboxRemember.isChecked) saveData()
                    val direction = AuthFragmentDirections.actionAuthFragmentToViewPagerFragment(textInputEditTextEmail.text.toString())
                    findNavController().navigate(direction)

                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()


    private fun isValidPassword(password: String): Boolean =
        password.length >= 7 && password.contains(Regex("[A-Z]")) &&
                password.contains(Regex("[a-z]")) &&
                password.contains(Regex("\\d")) && !password.contains(' ')

    private fun saveData() {
        val editor = sharedPreferences.edit()
        editor.putString(Constants.KEY_REMEMBER_ME, binding.textInputEditTextEmail.text.toString())
        editor.apply()
    }

    private fun dataValidation() {
        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, _, _, _ ->
                textViewInvalidEmail.visibleIf(
                    !isValidEmail(text.toString()) && !text.isNullOrEmpty()
                )
            }
            textInputEditTextPassword.doOnTextChanged { text, _, _, _ ->
                if (!text.isNullOrEmpty()) {
                    textViewInvalidPassword.visibility =
                        if (isValidPassword(text.toString())) View.INVISIBLE else View.VISIBLE
                } else {
                    textViewInvalidPassword.invisible()
                }
            }
        }
    }
}
