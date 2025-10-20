package com.tb.pdfly.page

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tb.pdfly.databinding.ActivityMainBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.fragments.CollectionFragment
import com.tb.pdfly.page.fragments.HistoryFragment
import com.tb.pdfly.page.fragments.HomeFragment
import com.tb.pdfly.page.fragments.SettingsFragment

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    override fun initView() {
        initViewPager()
        binding.btnCreate.setOnClickListener {

        }
    }


    private fun initViewPager() {
        setBottomButton(0)
        val fragments = listOf(HomeFragment(), HistoryFragment(), CollectionFragment(), SettingsFragment())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = fragments.size
        binding.viewPager.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setBottomButton(position)
            }
        })

        binding.btnHome.setOnClickListener { binding.viewPager.setCurrentItem(0, false) }
        binding.btnHistory.setOnClickListener { binding.viewPager.setCurrentItem(1, false) }
        binding.btnBookmarks.setOnClickListener { binding.viewPager.setCurrentItem(2, false) }
        binding.btnMine.setOnClickListener { binding.viewPager.setCurrentItem(3, false) }
    }


    private fun setBottomButton(position: Int) {
        binding.apply {

            when (position) {
                0 -> {
                    btnHome.isSelected = true
                    homeIndicator.isVisible = true
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                1 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = true
                    historyIndicator.isVisible = true
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                2 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = true
                    bookmarksIndicator.isVisible = true
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                3 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = true
                    mineIndicator.isVisible = true
                }
            }
        }
    }


}