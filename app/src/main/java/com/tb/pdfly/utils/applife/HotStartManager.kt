package com.tb.pdfly.utils.applife

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.guide.FirstLoadingActivity
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.parameter.toActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object HotStartManager {

    private var isHotStart = false
    private var isToSettingPage = false
    private var activityReferences = 0
    private var activityJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun onActivityStarted(activity: Activity) {
        activityReferences++
        activityJob?.cancel()

        if (isHotStart && isScreenInteractive()) {
            resetHotStart()
            if (isToSettingPage) {
                isToSettingPage = false
                return
            }

            if (activity is FirstLoadingActivity) return
            activity.toActivity<FirstLoadingActivity> {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        }
    }

    fun onActivityStopped(activity: Activity) {
        activityReferences--
        if (!isToSettingPage && activityReferences <= 0) {
            scheduleHotStart(activity)
        }
    }

    private fun scheduleHotStart(activity: Activity) {
        activityJob?.cancel()
        activityJob = coroutineScope.launch {
            runCatching {
                delay(3000L)
                isHotStart = true
                ActivityManager.finishSpecificActivities(MainActivity::class.java)
            }.onFailure {
                Log.e("HotStartManager", "Error during hot start scheduling", it)
            }
        }
    }

    fun isAppForeground() = activityReferences > 0

    fun resetHotStart() {
        isHotStart = false
    }

    fun navigateToSettingPage(boolean: Boolean) {
        isToSettingPage = boolean
    }

    fun isScreenInteractive(): Boolean {
        return runCatching { (app.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive }.getOrNull() ?: false
    }

    fun clear() {
        coroutineScope.cancel()
    }

}