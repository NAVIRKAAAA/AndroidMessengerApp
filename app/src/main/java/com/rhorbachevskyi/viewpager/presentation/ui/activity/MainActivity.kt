package com.rhorbachevskyi.viewpager.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.Message
import com.rhorbachevskyi.viewpager.databinding.ActivityMainBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseActivity
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import com.rhorbachevskyi.viewpager.presentation.utils.getLongFromPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleDeepLink(intent)
    }

    override fun setObservers() {
        val userId = getLongFromPrefs(Constants.USER_ID_KEY)
        lifecycleScope.launch {
            viewModel.messages.observe(this@MainActivity, Observer {
                if (viewModel.isMessageToMe(userId, it)) {
                    setMessage(it)
                }
            })
        }
    }

    private fun setMessage(message: Message) {
        val sender = viewModel.getContactById(message.senderId)
        with(binding.layoutMessage) {
            root.visible()
            imageViewUserPhoto.loadImage()
            textViewCareer.text = sender.career
            textViewName.text = sender.name
            textViewMessageText.text = message.text
            root.setOnClickListener { root.invisible() }
        }
    }


    private fun handleDeepLink(intent: Intent?) {
        if (intent?.data != null) {
            val navController =
                (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment).navController
            navController.navigate(R.id.searchFragment)
        }
    }
}