package com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager

import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentViewPagerBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter

class ViewPagerFragment :
    BaseFragment<FragmentViewPagerBinding>(FragmentViewPagerBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this@ViewPagerFragment)
        with(binding) {
            viewPager.offscreenPageLimit = 1
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    ViewPagerAdapter.Fragments.USER_PROFILE.ordinal -> getString(R.string.profile)
                    ViewPagerAdapter.Fragments.CONTACTS.ordinal -> getString(R.string.contacts)
                    else -> throw IllegalStateException("Unknown tab!")
                }
            }.attach()
        }
    }

    fun openFragment(index: Int) {
        binding.viewPager.currentItem = index
    }
}