package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore.saveData
import com.rhorbachevskyi.viewpager.presentation.utils.ext.gone
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUser.Success -> {
                        if (binding.checkboxRemember.isChecked) {
                            lifecycleScope.launch(Dispatchers.IO) {
                                saveData(
                                    requireContext(),
                                    binding.textInputEditTextEmail.text.toString(), binding.textInputEditTextPassword.text.toString()
                                )
                            }
                        }
                        val direction =
                            SignInFragmentDirections.actionSignInFragmentToViewPagerFragment(
                                UserWithTokens(
                                    it.userData.user,
                                    it.userData.accessToken,
                                    it.userData.refreshToken
                                )
                            )
                        navController.navigate(direction)
                    }
                    is ApiStateUser.Initial -> {

                    }
                    is ApiStateUser.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiStateUser.Error -> {
                        binding.progressBar.gone()
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }
                }
            }
        }
    }
}