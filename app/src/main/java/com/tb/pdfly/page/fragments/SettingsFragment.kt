package com.tb.pdfly.page.fragments

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.FragmentSettingsBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseFragment
import com.tb.pdfly.page.guide.LanguageActivity
import com.tb.pdfly.page.web.WebViewActivity
import com.tb.pdfly.parameter.PRIVACY_URL
import com.tb.pdfly.parameter.WEB_URL_KEY
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.applife.HotStartManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                requireActivity().toActivity<LanguageActivity> {
                    putExtra(LanguageActivity.INTENT_KEY, true)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Main) {
            delay(1000)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                ReportCenter.reportManager.report("tools_show_count")
                showMainNatAd()
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
            Intent.EXTRA_TEXT, getString(R.string.i_found_this_amazing_app_check_it_out, appLink)
        )
        HotStartManager.navigateToSettingPage(true)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_link_via)))
    }

    private var ad: IAd? = null
    private fun showMainNatAd() {
        if (AdCenter.adNoNeededShow()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_main_nat"))
        val nAd = AdCenter.pdflyMainNat
        val ac = requireActivity() as MainActivity
        nAd.waitingNativeAd(ac) {
            lifecycleScope.launch {
                while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
                if (nAd.canShow(ac)) {
                    binding?.adContainer?.apply {
                        ad?.destroy()
                        nAd.showNativeAd(ac, this, "pdfly_main_nat") {
                            ad = it
                        }
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