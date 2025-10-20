package com.tb.pdfly.utils.applife

import android.app.Application

object AppLifecycleUtils {

    private val lifecycleCallbacks = AppLifecycleCallbacksImpl()

    fun initialize(application: Application) {
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }

    fun unregister(application: Application) {
        application.unregisterActivityLifecycleCallbacks(lifecycleCallbacks)
        HotStartManager.clear()
    }

}