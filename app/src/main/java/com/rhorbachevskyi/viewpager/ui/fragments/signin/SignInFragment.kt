package com.rhorbachevskyi.viewpager.ui.fragments.signin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserRequest
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentSignInBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.utils.DataStore.saveData
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.utils.ext.visible
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


    private fun signUp() {
        binding.textViewSignUp.setOnClickListener {
            navController.navigateUp()
        }
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

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiState.Success -> {
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
                                UserWithTokens(it.userData.user, it.userData.accessToken, it.userData.refreshToken))
                        navController.navigate(direction)
                    }
                    is ApiState.Initial -> {

                    }
                    is ApiState.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }
                }
            }
        }
    }
}