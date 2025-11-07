package com.tb.pdfly.app

import android.app.Application
import cc.admaster.android.proxy.api.AdMasterConfig
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CommonUtils
import com.tb.pdfly.utils.CommonUtils.ioScope
import com.tb.pdfly.utils.RemoteConfigUtils
import com.tb.pdfly.utils.applife.AppLifecycleUtils
import com.tb.pdfly.utils.isSubscribedFcmTopic
import kotlinx.coroutines.launch

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        AppLifecycleUtils.initialize(this)
        RemoteConfigUtils.initRemoteConfig()
        initAdmob()
        initAdjust()
        initAdMaster()
        subscribeFCMIfNeed()
        CommonUtils.initCountryInfo()
        ReportCenter.infoManager.fetchAllInfo()
    }

    override fun onTerminate() {
        super.onTerminate()
        AppLifecycleUtils.unregister(this)
    }

    private fun initAdmob() {
        ioScope.launch {
            MobileAds.initialize(
                this@MyApp,
                InitializationConfig.Builder(if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544~3347511713" else "")
                    .setNativeValidatorDisabled()
                    .build()
            ) {
                "---Adapter initialization is complete---".showLog()
            }
        }
    }

    private fun initAdjust() = runCatching {
        Adjust.addGlobalCallbackParameter("customer_user_id", ReportCenter.mDistinctId)
        val (appToken, environment) = if (BuildConfig.DEBUG) {
            "ih2pm2dr3k74" to AdjustConfig.ENVIRONMENT_SANDBOX
        } else {
            "" to AdjustConfig.ENVIRONMENT_PRODUCTION
        }
        val config = AdjustConfig(this@MyApp, appToken, environment).apply {
            setLogLevel(LogLevel.WARN)
        }
        Adjust.initSdk(config)
    }.onFailure {
        it.printStackTrace()
    }

    private fun initAdMaster() = runCatching {
        AdMasterConfig.Builder().setAdMasterInitListener(object : AdMasterConfig.InitListener {
            override fun success() {}

            override fun fail(p0: Int, p1: String?) {}

        }).build(this@MyApp, "46ee2df008f022b6a7f105b77cedbf68").init()
    }

    private fun subscribeFCMIfNeed() {
        if (BuildConfig.DEBUG || isSubscribedFcmTopic) return
        runCatching { Firebase.messaging.subscribeToTopic("PDFLY_ALL").addOnSuccessListener { isSubscribedFcmTopic = true } }
    }

}