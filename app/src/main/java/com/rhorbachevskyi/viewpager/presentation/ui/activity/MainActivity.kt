package com.rhorbachevskyi.viewpager.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import com.rhorbachevskyi.viewpager.databinding.ActivityMainBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }
}