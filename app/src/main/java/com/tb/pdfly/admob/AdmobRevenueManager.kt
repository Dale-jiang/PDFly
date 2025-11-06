package com.tb.pdfly.admob

import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.google.android.libraries.ads.mobile.sdk.common.AdValue
import com.google.android.libraries.ads.mobile.sdk.common.ResponseInfo
import com.google.firebase.analytics.FirebaseAnalytics
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.total001Revenue
import java.util.Currency

object AdmobRevenueManager {

    fun onPaidEventListener(adValue: AdValue, responseInfo: ResponseInfo?, iAd: IAd) {

        ReportCenter.reportManager.reportAdImpressionEvent(adValue, responseInfo, iAd)
        ReportCenter.reportManager.report(
            "ad_impression_revenue",
            hashMapOf(FirebaseAnalytics.Param.VALUE to (adValue.valueMicros / 1000000.0), FirebaseAnalytics.Param.CURRENCY to "USD")
        )
        reportGoogle30(adValue.valueMicros)
        report2Firebase(adValue.valueMicros)
        report2FaceBook(adValue.valueMicros)
        report2Adjust(adValue, responseInfo)
    }

    fun onBannerPaidEventListener(adValue: AdValue, responseInfo: ResponseInfo?) {

        ReportCenter.reportManager.reportBannerAdImpressionEvent(adValue, responseInfo)
        ReportCenter.reportManager.report(
            "ad_impression_revenue",
            hashMapOf(FirebaseAnalytics.Param.VALUE to (adValue.valueMicros / 1000000.0), FirebaseAnalytics.Param.CURRENCY to "USD")
        )
        reportGoogle30(adValue.valueMicros)
        report2Firebase(adValue.valueMicros)
        report2FaceBook(adValue.valueMicros)
        report2Adjust(adValue, responseInfo)
    }

    private fun reportGoogle30(adValue: Long) = run {

        val revenue: Double = adValue / 1000000.0
        val updatedRevenue = total001Revenue + revenue
        val revenueThreshold = 0.01
        val currency = "USD"
        val eventName = "TotalAdRevenue001"

        if (updatedRevenue >= revenueThreshold) {
            runCatching {
                total001Revenue = 0.0
                ReportCenter.reportManager.report(eventName, hashMapOf(FirebaseAnalytics.Param.VALUE to updatedRevenue, FirebaseAnalytics.Param.CURRENCY to currency))
            }
        } else {
            total001Revenue = updatedRevenue
        }

    }

    private fun report2Adjust(adValue: AdValue, responseInfo: ResponseInfo?) = run {
        runCatching {
            Adjust.trackAdRevenue(AdjustAdRevenue("admob_sdk").also {
                it.setRevenue(adValue.valueMicros / 1000000.0, adValue.currencyCode)
                it.adRevenueNetwork = responseInfo?.loadedAdSourceResponseInfo?.name ?: ""
            })
        }
    }

    private fun report2Firebase(adValue: Long) = run {
        runCatching {
            if (!BuildConfig.DEBUG) {
                val revenue: Double = adValue / 1000000.0
                ReportCenter.firebaseAnalytics.logEvent("ad_impression_revenue", Bundle().apply {
                    putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                    putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                })
            }
        }
    }

    private fun report2FaceBook(adValue: Long) = run {
        runCatching {
            if (!BuildConfig.DEBUG) {
                val revenue: Double = adValue / 1000000.0
                ReportCenter.facebookLogger.logPurchase(revenue.toBigDecimal(), Currency.getInstance("USD"))
            }
        }
    }

}