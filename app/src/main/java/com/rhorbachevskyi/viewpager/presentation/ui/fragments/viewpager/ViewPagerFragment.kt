package com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.databinding.FragmentViewPagerBinding
import com.rhorbachevskyi.viewpager.presentation.ui.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import java.lang.IllegalStateException

class ViewPagerFragment :
    BaseFragment<FragmentViewPagerBinding>(FragmentViewPagerBinding::inflate) {

    private val args: ViewPagerFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this@ViewPagerFragment, args)
        with(binding) {
            viewPager.adapter = adapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    Constants.FIRST_FRAGMENT -> getString(R.string.profile)
                    Constants.SECOND_FRAGMENT -> getString(R.string.contacts)
                    else -> throw IllegalStateException("Unknown tab!")
                }
            }.attach()
        }
    }

    fun openFragment(index: Int) {
        binding.viewPager.currentItem = index
    }
}