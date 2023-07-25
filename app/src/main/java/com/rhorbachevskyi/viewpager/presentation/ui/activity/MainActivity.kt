package com.rhorbachevskyi.viewpager.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.ActivityMainBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: Intent?) {
        if (intent?.data != null) {
            val navController =
                (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment).navController
            navController.navigate(R.id.searchFragment)
        }
    }
}