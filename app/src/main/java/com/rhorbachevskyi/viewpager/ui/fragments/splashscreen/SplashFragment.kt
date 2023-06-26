package com.rhorbachevskyi.viewpager.ui.fragments.splashscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rhorbachevskyi.viewpager.utils.Constants
import com.rhorbachevskyi.viewpager.utils.DataStoreManager
import kotlinx.coroutines.launch

class SplashFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isAutologin()
        return super.onCreateView(inflater, container, savedInstanceState)
    }
    private fun isAutologin() {
        lifecycleScope.launch {
            val isRememberMe =
                DataStoreManager.getDataFromKey(requireContext(), Constants.KEY_REMEMBER_ME)
            if(isRememberMe != null) {
                val email = DataStoreManager.getDataFromKey(requireContext(), Constants.KEY_EMAIL)
                val direction = SplashFragmentDirections.actionSplashFragment2ToViewPagerFragment(email)
                findNavController().navigate(direction)
            } else {
                val direction = SplashFragmentDirections.actionSplashFragment2ToAuthFragment()
                findNavController().navigate(direction)
            }
        }
    }
}