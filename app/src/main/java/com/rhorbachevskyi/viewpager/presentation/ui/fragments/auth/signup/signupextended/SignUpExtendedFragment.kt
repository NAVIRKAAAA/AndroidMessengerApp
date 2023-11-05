package com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signup.signupextended

import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSignUpExtendedBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.saveToPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpExtendedFragment :
    BaseFragment<FragmentSignUpExtendedBinding>(FragmentSignUpExtendedBinding::inflate) {
    private val viewModel: SignUpExtendedViewModel by viewModels()
    private val args: SignUpExtendedFragmentArgs by navArgs()

    override fun setListeners() {
        with(binding) {
            buttonCancel.setOnClickListener { navController.navigateUp() }
            buttonForward.setOnClickListener { toUserProfileScreen() }
        }
    }

    private fun toUserProfileScreen() {
        with(binding) {
            if (viewModel.isNotValidUserName(textInputEditTextUserName.text.toString())) {
                root.showSnackBar(
                    requireContext(),
                    R.string.user_name_must_contain_at_least_3_letters
                )
            } else if (viewModel.isNotValidMobilePhone(textInputEditTextMobilePhone.text.toString())) {
                root.showSnackBar(
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


    override fun setObservers() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.registerState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiState.Success<*> -> {
                            if (args.rememberMe) {
                                requireContext().saveToPrefs( // email
                                    Constants.KEY_EMAIL,
                                    args.email
                                )
                                requireContext().saveToPrefs( // password
                                    Constants.KEY_PASSWORD,
                                    args.password
                                )
                            }
                            viewModel.isLogout()
                            val direction =
                                SignUpExtendedFragmentDirections.actionSignUpExtendedFragmentToViewPagerFragment()
                            navController.navigate(direction)
                        }

                        is ApiState.Loading -> {
                            progressBar.visible()
                        }

                        is ApiState.Initial -> {
                            textInputEditTextUserName.setText(viewModel.getNameFromEmail(args.email))
                        }

                        is ApiState.Error -> {
                            requireContext().showSnackBar(root, it.error)
                            viewModel.isLogout()
                            progressBar.invisible()
                        }
                    }
                }
            }
        }
    }
}