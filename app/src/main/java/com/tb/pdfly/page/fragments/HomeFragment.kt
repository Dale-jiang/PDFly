package com.tb.pdfly.page.fragments

import android.util.Log
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.FragmentHomeBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.FileType
import com.tb.pdfly.parameter.TabType
import com.tb.pdfly.parameter.dpToPx
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    private val viewModel by activityViewModels<GlobalVM>()


    override fun initView() {

        initViewPager()

        binding?.apply {

            viewPermission.btnAllow.setOnClickListener {
                viewModel.askPermissionLiveData.postValue(true)
            }

        }

        viewModel.onScanResultLiveData.observe(this) {
            binding?.loadingView?.isVisible = false
        }

        viewModel.showNoPermissionLiveData.observe(this) {
            binding?.apply {
                viewPermission.root.isVisible = it
                loadingView.isVisible = !it
            }
        }

    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                Log.e("AD_DEBUG", "HomeFragment onResume start")
                showMainNatAd()
                Log.e("AD_DEBUG", "HomeFragment onResume end")
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

    private var ad: IAd? = null
    private fun showMainNatAd() {
        if (AdCenter.adNoNeededShow()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_main_nat"))
        val nAd = AdCenter.pdflyMainNat
        val ac = requireActivity() as MainActivity
        nAd.waitingNativeAd(ac) {
            if (nAd.canShow(ac)) {
                binding?.adContainer?.apply {
                    ad?.destroy()
                    nAd.showNativeAd(ac, this, "pdfly_main_nat", 0) {
                        ad = it
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ad?.destroy()
    }


}