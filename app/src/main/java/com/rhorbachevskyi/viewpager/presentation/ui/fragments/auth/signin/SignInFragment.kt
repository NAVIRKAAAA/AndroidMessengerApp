package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore.saveData
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
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
            buttonLogin.setOnClickListener { login() }
            textViewSignUp.setOnClickListener { signUp() }
        }
    }

    private fun login() {
        with(binding) {
            viewModel.authorizationUser(
                textInputEditTextEmail.text.toString(),
                textInputEditTextPassword.text.toString()
            )
        }
    }

    private fun signUp() {
        val direction = SignInFragmentDirections.actionSignInFragmentToAuthFragment()
        navController.navigate(direction)
    }


    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        when (it) {
                            is ApiStateUser.Success<*> -> {
                                if (checkboxRemember.isChecked) {
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        saveData(
                                            requireContext(),
                                            textInputEditTextEmail.text.toString(),
                                            textInputEditTextPassword.text.toString()
                                        )
                                    }
                                }
                                val direction =
                                    SignInFragmentDirections.actionSignInFragmentToViewPagerFragment()
                                navController.navigate(direction)
                            }

                            is ApiStateUser.Initial -> Unit

                            is ApiStateUser.Loading -> {
                                progressBar.visible()
                            }

                            is ApiStateUser.Error -> {
                                progressBar.gone()
                                root.showErrorSnackBar(requireContext(), it.error)
                            }
                        }
                    }
            }
        }
    }
}