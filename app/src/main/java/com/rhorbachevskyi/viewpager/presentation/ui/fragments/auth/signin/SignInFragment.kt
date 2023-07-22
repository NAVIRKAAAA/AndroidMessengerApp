package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
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
        login()
        signUp()
    }
    private fun login() {
        with(binding) {
            buttonLogin.setOnClickListener {
                viewModel.authorizationUser(
                    UserRequest(
                        textInputEditTextEmail.text.toString(),
                        textInputEditTextPassword.text.toString()
                    )
                )
            }
        }
    }

    private fun signUp() {
        binding.textViewSignUp.setOnClickListener {
            val direction = SignInFragmentDirections.actionSignInFragmentToAuthFragment()
            navController.navigate(direction)
        }
    }



    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Success -> {
                            if (checkboxRemember.isChecked) {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    saveData(
                                        requireContext(),
                                        textInputEditTextEmail.text.toString(), textInputEditTextPassword.text.toString()
                                    )
                                }
                            }
                            val direction =
                                SignInFragmentDirections.actionSignInFragmentToViewPagerFragment()
                            navController.navigate(direction)
                        }
                        is ApiStateUser.Initial -> {

                        }
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