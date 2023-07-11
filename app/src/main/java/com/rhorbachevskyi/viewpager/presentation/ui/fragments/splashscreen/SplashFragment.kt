package com.rhorbachevskyi.viewpager.presentation.ui.fragments.splashscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.data.model.UserWithTokens
import com.rhorbachevskyi.viewpager.databinding.FragmentSplashScreenBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.auth.signin.SignInViewModel
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showErrorSnackBar
import kotlinx.coroutines.launch

class SplashFragment :
    BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {
    private val viewModel: SignInViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAutologin()
        setObserver()
    }

    private fun isAutologin() {
        lifecycleScope.launch {
            if (DataStore.getDataFromKey(requireContext(), Constants.KEY_REMEMBER_ME) != null) {
                viewModel.autoLogin(requireContext())
            } else {
                val direction = SplashFragmentDirections.actionSplashFragment2ToSignInFragment()
                navController.navigate(direction)
            }
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiStateUser.Success -> {
                        val direction =
                            SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment(
                                UserWithTokens(
                                    it.userData.user,
                                    it.userData.accessToken,
                                    it.userData.refreshToken
                                )
                            )
                        navController.navigate(direction)
                    }

                    is ApiStateUser.Loading -> {
                    }

                    is ApiStateUser.Initial -> {

                    }

                    is ApiStateUser.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }
                }
            }
        }
    }

}