package com.rhorbachevskyi.viewpager.ui.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpBinding
import com.rhorbachevskyi.viewpager.domain.model.UserRequest
import com.rhorbachevskyi.viewpager.ui.fragments.contact.ContactsViewModel
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
import com.rhorbachevskyi.viewpager.utils.Validation
import com.rhorbachevskyi.viewpager.utils.ext.invisible
import com.rhorbachevskyi.viewpager.utils.ext.visibleIf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        setListeners()
        setObservers()
        dataValidation()
        return binding.root
    }

    private fun setListeners() {
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        with(binding) {
            buttonRegister.setOnClickListener {
                if (Validation().isValidEmail(textInputEditTextEmail.text.toString()) &&
                    Validation().isValidPassword(textInputEditTextPassword.text.toString())
                ) {
                    if (checkboxRemember.isChecked) saveData()
                    viewModel.registerUser(
                        UserRequest(
                            textInputEditTextEmail.text.toString(),
                            textInputEditTextPassword.text.toString()
                        )
                    )
                    val direction = AuthFragmentDirections.actionAuthFragmentToViewPagerFragment(
                        textInputEditTextEmail.text.toString()
                    )
                    findNavController().navigate(direction)

                }
            }
        }
    }

    private fun setObservers() {
        lifecycleScope.launch {

        }
    }

    private fun saveData() {
        lifecycleScope.launch(Dispatchers.IO) {
            DataStoreManager.putData(
                requireContext(),
                Constants.KEY_EMAIL,
                binding.textInputEditTextEmail.text.toString()
            )
            DataStoreManager.putData(
                requireContext(),
                Constants.KEY_REMEMBER_ME,
                Constants.KEY_REMEMBER_ME
            )
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
