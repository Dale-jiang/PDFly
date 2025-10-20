package com.tb.pdfly.utils.applife

import android.app.Activity
import android.app.Application
import android.os.Bundle

class AppLifecycleCallbacksImpl : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ActivityManager.addActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        HotStartManager.onActivityStarted(activity)
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
        HotStartManager.onActivityStopped(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        ActivityManager.removeActivity(activity)
    }
}