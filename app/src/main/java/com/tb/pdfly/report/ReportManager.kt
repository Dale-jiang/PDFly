package com.tb.pdfly.report

import com.google.android.libraries.ads.mobile.sdk.common.AdValue
import com.google.android.libraries.ads.mobile.sdk.common.ResponseInfo
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.report.ReportCenter.buildCommonParams
import com.tb.pdfly.report.ReportCenter.getPrecisionType
import com.tb.pdfly.report.ReportCenter.postScope
import com.tb.pdfly.report.ReportCenter.reportKeys
import kotlinx.coroutines.launch
import org.json.JSONObject

class ReportManager : RequestExecutor() {

    fun report(event: String, params: Map<String, Any?> = emptyMap()) {
        postScope.launch {
            val jsonObj = buildCommonParams().apply {
                put("unite", event)
                params.forEach {
                    put("ordnance/${it.key}", it.value)
                }
            }
            runRequest("Report: $event", jsonObj.toString())
        }
    }

    fun reportSession() = run {
        postScope.launch {
            val jsonObj = buildCommonParams()
            jsonObj.put("ripley", JSONObject())
            runRequest("Report: session", jsonObj.toString())
        }
    }

    fun reportAdImpressionEvent(adValue: AdValue, responseInfo: ResponseInfo?, ad: IAd) {
        postScope.launch {
            runCatching {
                val jsonObj = buildCommonParams().apply {
                    put("cut", JSONObject().apply {
                        reportKeys["ad_pre_ecpm"]?.let { put(it, adValue.valueMicros) }
                        reportKeys["currency"]?.let { put(it, adValue.currencyCode) }
                        reportKeys["ad_network"]?.let { put(it, responseInfo?.loadedAdSourceResponseInfo?.name ?: "admob") }
                        reportKeys["ad_source_client"]?.let { put(it, ad.adItem?.adPlatform ?: "admob") }
                        reportKeys["ad_code_id"]?.let { put(it, ad.adItem?.adId ?: "") }
                        reportKeys["ad_pos_id"]?.let { put(it, ad.adPosition ?: "") }
                        reportKeys["ad_format"]?.let { put(it, ad.adItem?.adType ?: "") }
                        reportKeys["precision_type"]?.let { put(it, getPrecisionType(adValue.precisionType)) }
                    })
                }
                runRequest("Report: AdImpressionEvent", jsonObj.toString())
            }
        }
    }


}