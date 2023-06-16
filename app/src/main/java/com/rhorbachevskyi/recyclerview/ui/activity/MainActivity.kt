package com.rhorbachevskyi.recyclerview.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.recyclerview.R
import com.example.recyclerview.databinding.ActivityMainBinding
import com.rhorbachevskyi.recyclerview.domain.model.User
import com.rhorbachevskyi.recyclerview.ui.contract.Navigator
import com.rhorbachevskyi.recyclerview.ui.fragments.contact.UserFragment
import com.rhorbachevskyi.recyclerview.ui.fragments.userProfile.ProfileFragment

class MainActivity : AppCompatActivity(), Navigator {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        launchFragment(UserFragment())
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun showContactsScreen(user: User) {
        launchFragment(ProfileFragment(user))
    }
}
