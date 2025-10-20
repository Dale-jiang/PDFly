package com.tb.pdfly.utils.applife

import android.app.Activity
import android.util.Log
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

object ActivityManager {

    private val activities: MutableSet<Activity> = Collections.newSetFromMap(ConcurrentHashMap())

    fun addActivity(activity: Activity) {
        Log.d("=====>>>", "addActivity===" + activity::class.java.simpleName)
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        Log.d("=====>>>", "removeActivity===" + activity::class.java.simpleName)
        activities.remove(activity)
    }

    fun finishSpecificActivities(vararg classes: Class<*>) {
        activities.filter { it::class.java !in classes }
            .forEach { activity ->
                activity.finish()
            }
    }

    fun getActivities(): Set<Activity> = Collections.unmodifiableSet(activities)

}