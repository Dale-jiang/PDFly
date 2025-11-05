package com.tb.pdfly.report

import android.os.Build
import android.webkit.WebSettings
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter.CLOAK_URL
import com.tb.pdfly.report.ReportCenter.buildCommonParams
import com.tb.pdfly.report.ReportCenter.httpClient
import com.tb.pdfly.report.ReportCenter.postScope
import com.tb.pdfly.report.ReportCenter.reportKeys
import com.tb.pdfly.utils.adTrackEnable
import com.tb.pdfly.utils.cloakResult
import com.tb.pdfly.utils.googleIdStr
import com.tb.pdfly.utils.installReferrerStr
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class InfoManager : RequestExecutor() {

    private val maxRetries = 10
    private val delayCloak = 5_000L
    private val delayOthers = 10_000L
    private val jobMap = mutableMapOf<InfoType, Job?>()

    enum class InfoType { CLOAK, GOOGLE_ADS, REFERRER }

    fun fetchAllInfo() {
        fetchInfo(InfoType.CLOAK)
        fetchInfo(InfoType.GOOGLE_ADS)
        fetchInfo(InfoType.REFERRER)
    }

    private fun fetchInfo(type: InfoType) {

        if (!shouldContinue(type)) {
            "$type already fetched, skip.".showLog("InfoManager")
            return
        }

        if (jobMap[type]?.isActive == true) {
            "$type job already active, skip.".showLog("InfoManager")
            return
        }

        val job = postScope.launch {
            var retry = 0
            val delayTime = if (type == InfoType.CLOAK) delayCloak else delayOthers

            while (shouldContinue(type) && retry < maxRetries) {
                when (type) {
                    InfoType.CLOAK -> requestCloakInfo()
                    InfoType.GOOGLE_ADS -> requestGoogleAdsInfo()
                    InfoType.REFERRER -> requestReferrerInfo()
                }
                retry++
                delay(delayTime)
            }

            if (shouldContinue(type)) {
                "Failed to fetch $type after $maxRetries attempts.".showLog("InfoManager")
            } else {
                "$type fetched successfully.".showLog("InfoManager")
            }
        }

        jobMap[type] = job
    }

    private fun shouldContinue(type: InfoType): Boolean = when (type) {
        InfoType.CLOAK -> cloakResult.isBlank()
        InfoType.GOOGLE_ADS -> googleIdStr.isBlank()
        InfoType.REFERRER -> installReferrerStr.isBlank()
    }


    private fun requestCloakInfo() {
        try {
            val obj = JSONObject().apply {
                put(reportKeys["bundle_id"]!!, "com.pdfly.file")
                put(reportKeys["os"]!!, "squill")
                put(reportKeys["app_version"]!!, BuildConfig.VERSION_NAME)
                put(reportKeys["client_ts"]!!, System.currentTimeMillis())
            }

            val body = obj.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().post(body).url(CLOAK_URL).build()

            httpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    "CLOAK request failed: ${e.message}".showLog("InfoManager")
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let {
                        if (response.isSuccessful && it.isNotBlank()) {
                            cloakResult = it
                            "CLOAK result: $cloakResult".showLog("InfoManager")
                            jobMap[InfoType.CLOAK]?.cancel()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            "CLOAK exception: ${e.message}".showLog("InfoManager")
        }
    }

    private fun requestGoogleAdsInfo() {
        runCatching {
            "Fetching GOOGLE_ADS info ...".showLog("InfoManager")
            val adsInfo = AdvertisingIdClient.getAdvertisingIdInfo(app)
            googleIdStr = adsInfo.id ?: ""
            adTrackEnable = adsInfo.isLimitAdTrackingEnabled

            if (googleIdStr.isNotBlank()) {
                "GOOGLE_ADS id: $googleIdStr".showLog("InfoManager")
                jobMap[InfoType.GOOGLE_ADS]?.cancel()
            }
        }.onFailure {
            "GOOGLE_ADS error: ${it.message}".showLog("InfoManager")
        }
    }

    private fun requestReferrerInfo() {
        runCatching {
            val referrerClient = InstallReferrerClient.newBuilder(app).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val details = referrerClient.installReferrer
                            installReferrerStr = details?.installReferrer ?: ""
                            if (installReferrerStr.isNotBlank()) {
                                "requestReferrerInfo success: $installReferrerStr".showLog("InfoManager")
                                postInstall(details)
                                jobMap[InfoType.REFERRER]?.cancel()
                            }
                        }

                        else -> "Referrer setup failed: $responseCode".showLog("InfoManager")
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    "Referrer service disconnected.".showLog("InfoManager")
                }
            })
        }.onFailure {
            "requestReferrerInfo error: ${it.message}".showLog("InfoManager")
        }
    }


    private fun postInstall(details: ReferrerDetails?) {
        postScope.launch {

            val parameters = buildCommonParams()
            parameters.apply {
                put(reportKeys["build"]!!, "build/${Build.ID}")
                put(reportKeys["referrer_url"]!!, details?.installReferrer ?: "")
                put(reportKeys["install_version"]!!, details?.installVersion ?: "")
                put(reportKeys["user_agent"]!!, WebSettings.getDefaultUserAgent(app))
                put(reportKeys["lat"]!!, if (adTrackEnable) "proposal" else "symphony")
                put(reportKeys["referrer_click_timestamp_seconds"]!!, details?.referrerClickTimestampSeconds ?: 0L)
                put(reportKeys["install_begin_timestamp_seconds"]!!, details?.installBeginTimestampSeconds ?: 0L)
                put(reportKeys["referrer_click_timestamp_server_seconds"]!!, details?.referrerClickTimestampServerSeconds ?: 0L)
                put(reportKeys["install_begin_timestamp_server_seconds"]!!, details?.installBeginTimestampServerSeconds ?: 0L)
                put(reportKeys["install_first_seconds"]!!, 0L)
                put(reportKeys["last_update_seconds"]!!, 0L)
                put("unite", "diopter")
            }
            runRequest("REFERRER postInstall", parameters.toString())
        }
    }

}