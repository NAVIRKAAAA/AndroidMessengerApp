package com.rhorbachevskyi.viewpager.ui.fragments.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.lifecycle.Lifecycle
import com.rhorbachevskyi.viewpager.ui.fragments.contact.ContactsFragment
import com.rhorbachevskyi.viewpager.ui.fragments.userprofile.UserProfile
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentArgs

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val args: ViewPagerFragmentArgs
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> {
                val userProfileFragment = UserProfile()
                userProfileFragment.arguments = args.toBundle()
                userProfileFragment
            }
            else -> ContactsFragment()
        }
    }
}