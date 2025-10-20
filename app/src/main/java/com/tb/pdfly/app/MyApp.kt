package com.tb.pdfly.app

import android.app.Application
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.prefs
import com.tb.pdfly.utils.SharedPreferencesUtil
import com.tb.pdfly.utils.applife.AppLifecycleUtils

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        prefs = SharedPreferencesUtil.getInstance(this)
        AppLifecycleUtils.initialize(this)
    }


    override fun onTerminate() {
        super.onTerminate()
        AppLifecycleUtils.unregister(this)
    }
}