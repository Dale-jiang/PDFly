package com.tb.pdfly.report

import android.os.Build
import com.facebook.appevents.AppEventsLogger
import com.google.android.libraries.ads.mobile.sdk.common.PrecisionType
import com.google.firebase.analytics.FirebaseAnalytics
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.utils.distinctId
import com.tb.pdfly.utils.firstCountryCode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.util.Locale
import java.util.UUID

object ReportCenter {

    const val TAG = "ReportManager"
    const val CLOAK_URL = "https://dublin.readallpdffileviewer.net/bonanza/liniment/canon"
    val tbaUrl = if (BuildConfig.DEBUG) "https://test-dance.readallpdffileviewer.net/leighton/freshen/costume" else "https://dance.readallpdffileviewer.net/abut/lamar"

    val reportKeys = mapOf(
        "temp0" to "aaaaaaaaaaaaaaaaaaaaaaaaa",
        "bundle_id" to "auriga",
        "os" to "yoke",
        "app_version" to "mcguire",
        "distinct_id" to "spell",
        "log_id" to "staunton",
        "client_ts" to "bevy",
        "manufacturer" to "kenneth",
        "device_model" to "boreas",
        "os_version" to "wayward",
        "operator" to "mastery",
        "system_language" to "yore",
        "android_id" to "cognac",
        "os_country" to "egret",
        "gaid" to "exhaust",
        "temp1" to "xxxxxxxxxxxxxxxxxxxxxxxxxxx",
        "build" to "commute",
        "referrer_url" to "metaphor",
        "install_version" to "berwick",
        "user_agent" to "bourbon",
        "lat" to "gerund",
        "referrer_click_timestamp_seconds" to "dividend",
        "install_begin_timestamp_seconds" to "veldt",
        "referrer_click_timestamp_server_seconds" to "snafu",
        "install_begin_timestamp_server_seconds" to "pulley",
        "install_first_seconds" to "whey",
        "last_update_seconds" to "jacobson",
        "temp2" to "ssssssssssssssssssssssssssssss",
        "ad_pre_ecpm" to "ds",
        "currency" to "iffy",
        "ad_network" to "abolish",
        "ad_source_client" to "genital",
        "ad_code_id" to "ordeal",
        "ad_pos_id" to "wood",
        "ad_format" to "wiggly",
        "precision_type" to "layup",
        "temp3" to "nnnnnnnnnnnnnnnnnnnnnnnnnnnnnn",
    )

    val postScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { _, e -> "Coroutine error: ${e.message}".showLog("TAG") })
    }

    val httpClient by lazy { OkHttpClient.Builder().build() }
    val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(app) }
    val facebookLogger by lazy { AppEventsLogger.newLogger(app) }

    val mDistinctId: String by lazy { createDistinctId() }

    val infoManager by lazy { InfoManager() }
    val reportManager by lazy { ReportManager() }

    fun buildCommonParams(): JSONObject {
        return JSONObject().apply {
            put(reportKeys["bundle_id"]!!, "com.pdfly.file")
            put(reportKeys["os"]!!, "squill")
            put(reportKeys["app_version"]!!, BuildConfig.VERSION_NAME)
            put(reportKeys["distinct_id"]!!, mDistinctId)
            put(reportKeys["log_id"]!!, UUID.randomUUID().toString())
            put(reportKeys["client_ts"]!!, System.currentTimeMillis())
            put(reportKeys["manufacturer"]!!, Build.MANUFACTURER ?: "")
            put(reportKeys["device_model"]!!, Build.MODEL ?: "")
            put(reportKeys["os_version"]!!, Build.VERSION.RELEASE ?: "")
            put(reportKeys["operator"]!!, "")
            put(reportKeys["system_language"]!!, Locale.getDefault().toString())
            put(reportKeys["android_id"]!!, "")
            put(reportKeys["os_country"]!!, firstCountryCode)
        }
    }

    private fun createDistinctId(): String = distinctId.takeIf { it.isNotEmpty() } ?: UUID.randomUUID().toString().replace("-", "").also { distinctId = it }

    fun getPrecisionType(type: PrecisionType): String = run {
        return when (type) {
            PrecisionType.ESTIMATED -> "ESTIMATED"
            PrecisionType.PUBLISHER_PROVIDED -> "PUBLISHER_PROVIDED"
            PrecisionType.PRECISE -> "PRECISE"
            else -> "UNKNOWN"
        }
    }
}