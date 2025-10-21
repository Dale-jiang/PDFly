package com.tb.pdfly.page.fragments

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tb.pdfly.R
import com.tb.pdfly.databinding.FragmentHomeBinding
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.FileType
import com.tb.pdfly.parameter.TabType
import com.tb.pdfly.parameter.dpToPx

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by activityViewModels<GlobalVM>()


    override fun initView() {

        initViewPager()

        binding?.apply {

            viewPermission.btnAllow.setOnClickListener {
                viewModel.askPermissionLiveData.postValue(true)
            }


        }


        viewModel.showNoPermissionLiveData.observe(this) {
            binding?.apply {
                viewPermission.root.isVisible = it
                loadingView.isVisible = !it
            }
        }

    }

    private fun initViewPager() {
        val fragments = listOf(
            FileListFragment.newInstance(TabType.HOME, FileType.ALL),
            FileListFragment.newInstance(TabType.HOME, FileType.PDF),
            FileListFragment.newInstance(TabType.HOME, FileType.WORD),
            FileListFragment.newInstance(TabType.HOME, FileType.EXCEL),
            FileListFragment.newInstance(TabType.HOME, FileType.PPT)

        )
        val titles = listOf(
            getString(R.string.all),
            getString(R.string.pdf),
            getString(R.string.word),
            getString(R.string.excel),
            getString(R.string.ppt)
        )
        val viewPagerAdapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }

            override fun getItemCount(): Int = fragments.size
        }
        binding?.apply {
            viewPager.offscreenPageLimit = fragments.size
            viewPager.adapter = viewPagerAdapter
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
            setTabLayout()
        }
    }

    private fun setTabLayout() {
        binding?.apply {
            val tabs = tabLayout.getChildAt(0) as ViewGroup
            for (i in 0 until tabs.childCount) {
                val tab = tabs.getChildAt(i)
                val lp = tab.layoutParams as ViewGroup.MarginLayoutParams
                lp.marginEnd = requireActivity().dpToPx(5f).toInt()
                lp.marginStart = requireActivity().dpToPx(5f).toInt()
                tab.layoutParams = lp
                tabLayout.requestLayout()
            }
        }
    }

}