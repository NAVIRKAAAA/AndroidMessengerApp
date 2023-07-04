package com.rhorbachevskyi.viewpager.ui.fragments.splashscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.databinding.FragmentSplashScreenBinding
import com.rhorbachevskyi.viewpager.domain.utils.ApiState
import com.rhorbachevskyi.viewpager.ui.BaseFragment
import com.rhorbachevskyi.viewpager.ui.fragments.signin.SignInViewModel
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStore
import com.rhorbachevskyi.viewpager.utils.ext.showErrorSnackBar
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
            val isRememberMe =
                DataStore.getDataFromKey(requireContext(), Constants.KEY_REMEMBER_ME)
            if (isRememberMe != null) {
                viewModel.autoLogin(requireContext())
            } else {
                val direction = SplashFragmentDirections.actionSplashFragment2ToAuthFragment()
                navController.navigate(direction)
            }
        }
    }

    private fun setObserver() {
        lifecycleScope.launch {
            viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                when (it) {
                    is ApiState.Success -> {
                        val direction =
                            SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment(it.userData.user)
                        navController.navigate(direction)
                    }

                    is ApiState.Loading -> {
                    }
                    is ApiState.Initial -> {

                    }
                    is ApiState.Error -> {
                        binding.root.showErrorSnackBar(requireContext(), it.error)
                    }
                }
            }
        }
    }

}