package com.tb.pdfly.page.fragments

import android.view.ViewGroup
import com.tb.pdfly.databinding.FragmentHomeBinding
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.parameter.dpToPx

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun initView() {

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


        binding?.apply {

            viewPermission.btnAllow.setOnClickListener {




            }


        }


    }

}