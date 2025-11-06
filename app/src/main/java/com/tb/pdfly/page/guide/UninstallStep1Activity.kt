package com.tb.pdfly.page.guide

import android.content.Intent
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.ActivityUninstallStep1Binding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UninstallStep1Activity : BaseActivity<ActivityUninstallStep1Binding>(ActivityUninstallStep1Binding::inflate) {


    override fun initView() {

        onBackPressedDispatcher.addCallback {
            goBackHome()
        }

        binding.apply {
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            layoutRetry.setOnClickListener { goBackHome() }
            layoutDiscover.setOnClickListener { goBackHome() }
            btnKeep.setOnClickListener { goBackHome() }
            btnUninstall.setOnClickListener {
                showNextAd {
                    toActivity<UninstallStep2Activity>(finishCurrent = true)
                }
            }
        }

        AdCenter.pdflyBackInt.loadAd(app)
        showNatAd()

    }


    private fun goBackHome() {
        toActivity<MainActivity>(finishCurrent = true) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }

    private fun showNextAd(callBack: CallBack) {
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_uninstall_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyScanInt
            if (ad.canShow(this@UninstallStep1Activity)) {
                ad.showFullAd(this@UninstallStep1Activity, "pdfly_uninstall_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@UninstallStep1Activity)
                callBack()
            }
        }
    }

    private var ad: IAd? = null
    private fun showNatAd() {
        if (AdCenter.adNoNeededShow()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_uninstall_nat1"))
        val nAd = AdCenter.pdflyMainNat
        nAd.waitingNativeAd(this@UninstallStep1Activity) {
            lifecycleScope.launch {
                while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
                if (nAd.canShow(this@UninstallStep1Activity)) {
                    binding.adContainer.apply {
                        ad?.destroy()
                        nAd.showNativeAd(this@UninstallStep1Activity, this, "pdfly_uninstall_nat1") {
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