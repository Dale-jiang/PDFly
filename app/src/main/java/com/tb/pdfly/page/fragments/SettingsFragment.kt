package com.tb.pdfly.page.fragments

import android.content.Intent
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.R
import com.tb.pdfly.databinding.FragmentSettingsBinding
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.web.WebViewActivity
import com.tb.pdfly.parameter.PRIVACY_URL
import com.tb.pdfly.parameter.WEB_URL_KEY
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.utils.applife.HotStartManager

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    override fun initView() {

        binding?.apply {

            itemPrivacy.setOnClickListener {
                requireActivity().toActivity<WebViewActivity> {
                    putExtra(WEB_URL_KEY, PRIVACY_URL)
                }
            }

            itemShare.setOnClickListener {
                shareAppLink()
            }

            itemLanguage.setOnClickListener {

            }


        }
    }

    private fun shareAppLink() = runCatching {
        val packages = if (BuildConfig.DEBUG) "com.tb.pdfly" else "com.pdfly.file"
        val appLink = "https://play.google.com/store/apps/details?id=$packages"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.check_out_this_app))
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            getString(R.string.i_found_this_amazing_app_check_it_out, appLink)
        )
        HotStartManager.navigateToSettingPage(true)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_link_via)))
    }


}