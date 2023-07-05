package com.rhorbachevskyi.viewpager.ui.fragments.viewpager.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rhorbachevskyi.viewpager.ui.fragments.contact.ContactsFragment
import com.rhorbachevskyi.viewpager.ui.fragments.userprofile.UserProfile
import com.rhorbachevskyi.viewpager.ui.fragments.viewpager.ViewPagerFragmentArgs
import com.rhorbachevskyi.viewpager.utils.Constants

class ViewPagerAdapter(
    fragment: Fragment,
    private val args: ViewPagerFragmentArgs
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = Constants.FRAGMENT_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (Fragments.values()[position]) {
            Fragments.USER_PROFILE -> {
                val userProfileFragment = UserProfile()
                userProfileFragment.arguments = args.toBundle()
                userProfileFragment
            }
            Fragments.CONTACTS -> {
                val contactsFragment = ContactsFragment()
                contactsFragment.arguments = args.toBundle()
                contactsFragment
            }
        }
    }
    enum class Fragments {
        USER_PROFILE,
        CONTACTS
    }
}