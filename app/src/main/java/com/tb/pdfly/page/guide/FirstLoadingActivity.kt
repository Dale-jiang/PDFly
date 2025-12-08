package com.tb.pdfly.page.guide

import android.content.Intent
import android.os.Build
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.ActivityFirstLoadingBinding
import com.tb.pdfly.notice.FrontNoticeManager.KEY_NOTICE_CONTENT
import com.tb.pdfly.notice.NoticeContent
import com.tb.pdfly.notice.NoticeType
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.isGrantedPostNotification
import com.tb.pdfly.parameter.myEnableEdgeToEdge
import com.tb.pdfly.parameter.showFrontNotice
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CountdownTimer
import com.tb.pdfly.utils.firstCountryCode
import com.tb.pdfly.utils.hasRequestUMP
import com.tb.pdfly.utils.isFirstLaunch
import com.tb.pdfly.utils.isRequestNotice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class FirstLoadingActivity : BaseActivity<ActivityFirstLoadingBinding>(ActivityFirstLoadingBinding::inflate) {

    private val noticeResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (hasRequestUMP) startLoading() else doUmpRequest()
    }

    private val keyUninstall by lazy { intent?.getStringExtra("key_uninstall") }
    private val noticeContent by lazy { intent?.getParcelableExtra<NoticeContent>(KEY_NOTICE_CONTENT) }

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

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000L)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                "------------------FirstLoadingActivity onResume--------------------".showLog("AAAA")
                ReportCenter.reportManager.reportSession()
                if (isFirstLaunch) ReportCenter.reportManager.report("first_loading_show_count")
                ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_launch"))

                if (isGrantedPostNotification() || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    if (hasRequestUMP) startLoading() else doUmpRequest()
                    lifecycleScope.launch(Dispatchers.Main) {
                        delay(1000L)
                        showFrontNotice()
                    }
                } else {
                    if (!isRequestNotice) {
                        isRequestNotice = true
                        noticeResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(this@FirstLoadingActivity, android.Manifest.permission.POST_NOTIFICATIONS)) {
                        noticeResultLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    } else if (hasRequestUMP) startLoading() else doUmpRequest()
                }

                noticeContent?.apply {
                    runCatching {
                        NotificationManagerCompat.from(app).cancel(this.notificationId)
                    }
                    when (this.noticeType) {
                        NoticeType.FRONT -> {
                            ReportCenter.reportManager.report("loading_show_count", hashMapOf("list" to "noti"))
                            ReportCenter.reportManager.report("persistent_notification_click_count")
                        }

                        NoticeType.MESSAGE -> {
                            ReportCenter.reportManager.report("loading_show_count", hashMapOf("list" to "popup"))
                            if (this.triggerType == "time") {
                                ReportCenter.reportManager.report("notification_click_count", hashMapOf("list" to "times"))
                            } else if (this.triggerType == "alarm") {
                                ReportCenter.reportManager.report("notification_click_count", hashMapOf("list" to "alarm"))
                            } else {
                                ReportCenter.reportManager.report("notification_click_count", hashMapOf("list" to "lock"))
                            }
                        }
                    }
                }

            }
        }
    }

    override fun initView() {

        onBackPressedDispatcher.addCallback { }

    }

    private fun startLoading() {
        AdCenter.pdflyLaunch.loadLaunchAd(this)
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
        AdCenter.pdflyMainNat.loadAd(app)
        AdCenter.pdflyScanInt.loadAd(app)
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
                startActivities(
                    arrayOf(
                        Intent(this, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        },
                        Intent(this, UninstallStep1Activity::class.java),
                    )
                )
                finish()
            }

            else -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true) {
                        putExtra(KEY_NOTICE_CONTENT, noticeContent)
                    }
                }
            }
        }


    }

    private fun toGuideIfNeeded(next: () -> Unit) {

        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) return

        if (isFirstLaunch) {
            isFirstLaunch = false

            startActivities(
                arrayOf(
                    Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    },
                    Intent(this, LanguageActivity::class.java).apply {
                        putExtra(LanguageActivity.INTENT_KEY, false)
                    },
                )
            )
            finish()
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