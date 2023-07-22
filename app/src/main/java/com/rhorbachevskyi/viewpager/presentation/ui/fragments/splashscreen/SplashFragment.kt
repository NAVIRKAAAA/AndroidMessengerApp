package com.rhorbachevskyi.viewpager.presentation.ui.fragments.splashscreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentSplashScreenBinding
import com.rhorbachevskyi.viewpager.domain.states.ApiStateUser
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
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
            if (DataStore.getDataFromKey(requireContext(), Constants.KEY_REMEMBER_ME) != null) {
                viewModel.autoLogin(requireContext())
            } else {
                val direction = SplashFragmentDirections.actionSplashFragment2ToSignInFragment()
                navController.navigate(direction)
            }
        }
    }

    private fun setObserver() {
        with(binding) {
            lifecycleScope.launch {
                viewModel.authorizationState.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
                    when (it) {
                        is ApiStateUser.Success -> {
                            val direction =
                                SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment()
                            navController.navigate(direction)
                        }

                        is ApiStateUser.Loading -> {
                            progressBar.visible()
                        }

                        is ApiStateUser.Initial -> {}

                        is ApiStateUser.Error -> {
                            progressBar.invisible()
                            Snackbar.make(
                                root,
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
}