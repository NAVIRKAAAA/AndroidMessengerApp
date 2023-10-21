package com.rhorbachevskyi.viewpager.presentation.ui.fragments.splashscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSplashScreenBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiState
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment :
    BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {
    private val viewModel: SplashScreenViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAutologin()
        setObserver()
    }

    private fun isAutologin() {
        lifecycleScope.launch {
            if (viewModel.isAutoLogin(requireContext())) {
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
                    is ApiState.Success<*> -> {
                        val direction =
                            SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment()
                        navController.navigate(direction)
                    }

                    is ApiState.Loading -> {
                        binding.progressBar.visible()
                    }

                    is ApiState.Initial -> {}

                    is ApiState.Error -> {
                        binding.progressBar.invisible()
                        Snackbar.make(
                            binding.root,
                            R.string.No_internet_connection,
                            Snackbar.LENGTH_INDEFINITE
                        ).setAction(R.string.repeat) {
                            viewModel.autoLogin(requireContext())
                        }.show()
                    }
                }
            }
        }
    }
}