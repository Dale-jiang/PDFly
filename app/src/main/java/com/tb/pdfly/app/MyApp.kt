package com.tb.pdfly.app

import android.app.Application
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
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
    }

    override fun onTerminate() {
        super.onTerminate()
        AppLifecycleUtils.unregister(this)
    }

    private fun initAdmob() {
        ioScope.launch {
            MobileAds.initialize(
                this@MyApp,
                InitializationConfig.Builder(if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544~3347511713" else "").build()
            ) {
                "---Adapter initialization is complete---".showLog()
            }
        }
    }
}