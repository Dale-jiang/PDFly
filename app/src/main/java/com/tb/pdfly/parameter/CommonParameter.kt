package com.tb.pdfly.parameter

import com.tb.pdfly.app.MyApp
import com.tb.pdfly.utils.SharedPreferencesUtil
import java.util.UUID

internal lateinit var app: MyApp
internal lateinit var prefs: SharedPreferencesUtil

val mAndroidId: String by lazy {
    prefs.getString(ANDROID_ID).takeUnless { it.isEmpty() } ?: run {
        val newId = UUID.randomUUID().toString().replace("-", "")
        prefs.putString(ANDROID_ID, newId)
        newId
    }
}


internal const val PRIVACY_URL = "https://www.baidu.com"
internal const val EMAIL = ""


internal const val WEB_URL_KEY = "WEB_URL_KEY"

/**----------------sp key----------------------**/
internal const val FIRST_LAUNCH = "FIRST_LAUNCH"
internal const val ANDROID_ID = "ANDROID_ID"