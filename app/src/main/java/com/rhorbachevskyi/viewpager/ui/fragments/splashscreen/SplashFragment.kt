package com.rhorbachevskyi.viewpager.ui.fragments.splashscreen

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.databinding.FragmentSplashScreenBinding
import com.rhorbachevskyi.viewpager.ui.fragments.BaseFragment
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
import kotlinx.coroutines.launch

class SplashFragment :
    BaseFragment<FragmentSplashScreenBinding>(FragmentSplashScreenBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isAutologin()
    }

    private fun isAutologin() {
        lifecycleScope.launch {
            val isRememberMe =
                DataStoreManager.getDataFromKey(requireContext(), Constants.KEY_REMEMBER_ME)
            if (isRememberMe != null) {
                val email = DataStoreManager.getDataFromKey(requireContext(), Constants.KEY_EMAIL)
                val direction =
                    SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment(email)
                navController.navigate(direction)
            } else {
                val direction = SplashFragmentDirections.actionSplashFragment2ToAuthFragment()
                navController.navigate(direction)
            }
        }
    }
}