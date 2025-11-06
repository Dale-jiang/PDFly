package com.tb.pdfly.admob.ad

import android.content.Context
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAd
import com.google.android.libraries.ads.mobile.sdk.appopen.AppOpenAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdValue
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.AdmobRevenueManager.onPaidEventListener
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.showLog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullAdImpl(override var adPosition: String?, override var adItem: AdConfigItem? = null, override var adLoadTime: Long) : IAd {

    private var mAd: Any? = null

    override fun loadAd(context: Context, onLoaded: (success: Boolean, msg: String?) -> Unit) {
        when (adItem?.adType) {
            "int" -> loadInterstitial(onLoaded)
            "op" -> loadAppOpen(onLoaded)
            else -> onLoaded.invoke(false, "------>>ad type is wrong<<------")
        }
    }

    private fun loadInterstitial(cb: (Boolean, String?) -> Unit) {
        adItem?.run {
            InterstitialAd.load(AdRequest.Builder(adId).build(), object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    mAd = ad
                    adLoadTime = System.currentTimeMillis()
                    ad.adEventCallback
                    cb(true, null)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mAd = null
                    cb(false, adError.message)
                }
            })
        }
    }

    private fun loadAppOpen(cb: (Boolean, String?) -> Unit) {
        adItem?.run {
            AppOpenAd.load(AdRequest.Builder(adId).build(), object : AdLoadCallback<AppOpenAd> {
                override fun onAdLoaded(ad: AppOpenAd) {
                    mAd = ad
                    adLoadTime = System.currentTimeMillis()
                    cb(true, null)
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mAd = null
                    cb(false, adError.message)
                }
            })
        }
    }


    override fun showAd(activity: BaseActivity<*>, parent: ViewGroup?, nativeType: Int, onClose: () -> Unit) {
        when (val a = mAd) {
            is InterstitialAd -> {

                a.adEventCallback = object : InterstitialAdEventCallback {
                    override fun onAdDismissedFullScreenContent() = onAdClose(activity, onClose)
                    override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                        onAdClose(activity, onClose)
                        "$adPosition -- ${adItem?.adType} -- ${adItem?.adId} show failed:${fullScreenContentError.message}".showLog()
                    }

                    override fun onAdClicked() {
                        // PostCenter.postCustomEvent("ew_ad_click_v")
                    }

                    override fun onAdPaid(value: AdValue) {
                        onPaidEventListener(value, a.getResponseInfo(), this@FullAdImpl)
                    }
                }

                a.show(activity)
            }


            is AppOpenAd -> {
                a.adEventCallback = object : AppOpenAdEventCallback {

                    override fun onAdDismissedFullScreenContent() = onAdClose(activity, onClose)

                    override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                        onAdClose(activity, onClose)
                        "$adPosition -- ${adItem?.adType} -- ${adItem?.adId} show failed:${fullScreenContentError.message}".showLog()
                    }

                    override fun onAdClicked() {
                        //PostCenter.postCustomEvent("ew_ad_click_v")
                    }


                    override fun onAdPaid(value: AdValue) {
                        onPaidEventListener(value, a.getResponseInfo(), this@FullAdImpl)
                    }

                }
                a.show(activity)
            }

            else -> onAdClose(activity, onClose)
        }
    }

    private fun onAdClose(activity: BaseActivity<*>?, close: () -> Unit = {}) {
        activity?.lifecycleScope?.launch {
            while (!activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(100L)
            close.invoke()
        } ?: close.invoke()
    }

    override fun destroy() = Unit

}