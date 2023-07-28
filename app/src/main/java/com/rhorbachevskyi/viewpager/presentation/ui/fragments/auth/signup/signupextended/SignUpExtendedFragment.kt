package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpExtendedFragment :
    BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) {
    private val viewModel: SignUpExtendedViewModel by viewModels()
    private val args: SignUpExtendedFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setSignUpExtended()
        setObservers()
    }

    private fun setListeners() {
        with(binding) {
            buttonCancel.setOnClickListener { toSignUpScreen() }
            buttonForward.setOnClickListener { toUserProfile() }
        }
    }

    private fun toSignUpScreen() {
        navController.navigateUp()
    }

    private fun toUserProfile() {
        with(binding) {
            if (viewModel.isNotValidUserName(textInputEditTextUserName.text.toString())) {
                root.showErrorSnackBar(
                    requireContext(),
                    R.string.user_name_must_contain_at_least_3_letters
                )
            } else if (viewModel.isNotValidMobilePhone(textInputEditTextMobilePhone.text.toString())) {
                root.showErrorSnackBar(
                    requireContext(),
                    R.string.phone_must_be_at_least_10_digits_long
                )
            } else {
                viewModel.registerUser(
                    args.email,
                    args.password,
                    textInputEditTextUserName.text.toString(),
                    textInputEditTextMobilePhone.text.toString()
                )
            }
        }
    }

    private fun setSignUpExtended() {
        binding.textInputEditTextUserName.setText(viewModel.getNameFromEmail(args.email))
    }

    private fun setObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.registerState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Success<*> -> {
                            if (args.rememberMe) {
                                viewModel.saveUserDataToDataStore(
                                    requireContext(),
                                    args.email,
                                    args.password
                                )
                            }
                            viewModel.isLogout()
                            val direction =
                                SignUpExtendedFragmentDirections.actionSignUpExtendedFragmentToViewPagerFragment()
                            navController.navigate(direction)
                        }

                        is ApiStateUser.Loading -> {
                            progressBar.visible()
                        }

                        is ApiStateUser.Initial -> {}

                        is ApiStateUser.Error -> {
                            root.showErrorSnackBar(requireContext(), it.error)
                            viewModel.isLogout()
                            progressBar.invisible()
                        }
                    }
                }
            }
        }
    }
}