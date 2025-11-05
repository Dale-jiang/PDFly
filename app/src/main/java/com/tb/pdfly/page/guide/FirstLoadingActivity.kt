package com.tb.pdfly.page.guide

import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.ActivityFirstLoadingBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.myEnableEdgeToEdge
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CountdownTimer
import com.tb.pdfly.utils.firstCountryCode
import com.tb.pdfly.utils.hasRequestUMP
import com.tb.pdfly.utils.isFirstLaunch
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FirstLoadingActivity : BaseActivity<ActivityFirstLoadingBinding>(ActivityFirstLoadingBinding::inflate) {

    private val keyUninstall by lazy { intent?.getStringExtra("key_uninstall") }

    @Suppress("DEPRECATION")
    private val countdownTimer by lazy {
        CountdownTimer(
            totalTimeMillis = 10_000L,
            intervalMillis = 1000L,
            onTick = { millisUntilFinished ->
                val secondsRemaining = millisUntilFinished / 1000
                if ((secondsRemaining < 8 && AdCenter.pdflyLaunch.canShow(this@FirstLoadingActivity)) || millisUntilFinished == 0L) {
                    showOpenAd {
                        doNext()
                    }
                }
            },
            onFinish = {
                doNext()
            }
        )
    }

    override fun onAttachedToWindow() {
        myEnableEdgeToEdge(binding.container, topPadding = true, bottomPadding = true)
    }

    override fun initView() {
        ReportCenter.reportManager.reportSession()
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_launch"))
        onBackPressedDispatcher.addCallback { }
        if (hasRequestUMP) startLoading() else doUmpRequest()
    }

    private fun startLoading() {
        AdCenter.pdflyLaunch.loadAd(this)
        hasRequestUMP = true
        countdownTimer.start()
    }

    private fun doUmpRequest() {
        if (firstCountryCode !in AdCenter.countryCodeList) {
            startLoading()
            return
        }

        val paramsBuilder = ConsentRequestParameters.Builder()
        if (BuildConfig.DEBUG) {
            val debugSettings = ConsentDebugSettings.Builder(this)
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .addTestDeviceHashedId("")
                .build()
            paramsBuilder.setConsentDebugSettings(debugSettings)
        }
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation.requestConsentInfoUpdate(this, paramsBuilder.build(), {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { startLoading() }
        }, { startLoading() }
        )
        if (BuildConfig.DEBUG) {
            consentInformation.reset()
        }
    }


    private fun doNext() {
        countdownTimer.stop()
        when (keyUninstall) {
            "shortcut_view" -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }

            "shortcut_scan" -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }

            "shortcut_uninstall" -> {
                toActivity<UninstallStep1Activity>(finishCurrent = true)
            }

            else -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }
        }


    }

    private fun toGuideIfNeeded(next: () -> Unit) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            toActivity<LanguageActivity>(finishCurrent = true) {
                putExtra(LanguageActivity.INTENT_KEY, false)
            }
            return
        }
        next.invoke()
    }

    private fun showOpenAd(callBack: CallBack) {
        countdownTimer.stop()
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyLaunch
            if (ad.canShow(this@FirstLoadingActivity)) {
                ad.showFullAd(this@FirstLoadingActivity, "pdfly_launch", showLoading = false) { callBack() }
            } else {
                ad.loadAd(this@FirstLoadingActivity)
                callBack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

}