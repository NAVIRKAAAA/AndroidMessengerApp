package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding>(FragmentSignInBinding::inflate) {
    private val viewModel: SignInViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObserver()
    }

    private fun setListeners() {
        with(binding) {
            buttonLogin.setOnClickListener { signInUser() }
            textViewSignUp.setOnClickListener { toSignUpScreen() }
        }
    }

    private fun signInUser() {
        with(binding) {
            viewModel.authorizationUser(
                textInputEditTextEmail.text.toString(),
                textInputEditTextPassword.text.toString()
            )
        }
    }

    private fun toSignUpScreen() {
        val direction = SignInFragmentDirections.actionSignInFragmentToAuthFragment()
        navController.navigate(direction)
    }


    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        when (it) {
                            is ApiState.Success<*> -> {
                                if (checkboxRemember.isChecked) {
                                    viewModel.saveUserDataToDataStore(
                                        requireContext(),
                                        textInputEditTextEmail.text.toString(),
                                        textInputEditTextPassword.text.toString()
                                    )
                                }
                                val direction =
                                    SignInFragmentDirections.actionSignInFragmentToViewPagerFragment()
                                navController.navigate(direction)
                            }

                            is ApiState.Initial -> {}

                            is ApiState.Loading -> {
                                progressBar.visible()
                            }

                            is ApiState.Error -> {
                                progressBar.gone()
                                root.showErrorSnackBar(requireContext(), it.error)
                            }
                        }
                    }
            }
        }
    }
}