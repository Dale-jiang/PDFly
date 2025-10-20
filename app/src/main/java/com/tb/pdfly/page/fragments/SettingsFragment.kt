package com.tb.pdfly.page.fragments

import com.tb.pdfly.databinding.FragmentSettingsBinding
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.web.WebViewActivity
import com.tb.pdfly.parameter.PRIVACY_URL
import com.tb.pdfly.parameter.WEB_URL_KEY
import com.tb.pdfly.parameter.toActivity

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun initView() {

        binding?.apply {

            itemPrivacy.setOnClickListener {
                requireActivity().toActivity<WebViewActivity> {
                    putExtra(WEB_URL_KEY, PRIVACY_URL)
                }
            }

            itemShare.setOnClickListener {

            }

            itemLanguage.setOnClickListener {

            }


        }
    }

}