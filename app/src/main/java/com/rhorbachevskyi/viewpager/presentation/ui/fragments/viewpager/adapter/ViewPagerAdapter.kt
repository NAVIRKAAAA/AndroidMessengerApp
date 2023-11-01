package com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.contact.ContactsFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile.UserProfile

class ViewPagerAdapter(
    fragment: Fragment
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = Fragments.values().size

    override fun createFragment(position: Int): Fragment =
        when (Fragments.values()[position]) {
            Fragments.USER_PROFILE -> UserProfile()
            Fragments.CONTACTS -> ContactsFragment()
        }


    enum class Fragments {
        USER_PROFILE,
        CONTACTS
    }
}