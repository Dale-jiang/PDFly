package com.tb.pdfly.page

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.tb.pdfly.databinding.ActivityMainBinding
import com.tb.pdfly.page.base.BaseFilePermissionActivity
import com.tb.pdfly.page.fragments.CollectionFragment
import com.tb.pdfly.page.fragments.HistoryFragment
import com.tb.pdfly.page.fragments.HomeFragment
import com.tb.pdfly.page.fragments.SettingsFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.hasStoragePermission

class MainActivity : BaseFilePermissionActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel by viewModels<GlobalVM>()

    override fun initView() {

        if (hasStoragePermission()) {
            viewModel.showNoPermissionLiveData.postValue(false)
            //viewModel.scanFiles(getBaseActivity())
        } else {
            viewModel.showNoPermissionLiveData.postValue(true)
        }

        initViewPager()
        binding.btnCreate.setOnClickListener {

        }

        viewModel.askPermissionLiveData.observe(this) {
            checkStoragePermission()
        }

    }

    override fun onStoragePermissionGranted() {
        viewModel.showNoPermissionLiveData.postValue(false)
        //todo
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