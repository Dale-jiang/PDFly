package com.tb.pdfly.app

import android.app.Application
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CommonUtils
import com.tb.pdfly.utils.CommonUtils.ioScope
import com.tb.pdfly.utils.RemoteConfigUtils
import com.tb.pdfly.utils.applife.AppLifecycleUtils
import kotlinx.coroutines.launch

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        AppLifecycleUtils.initialize(this)
        RemoteConfigUtils.initRemoteConfig()
        initAdmob()
        initAdjust()
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

    fun initAdjust() = runCatching {
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

}