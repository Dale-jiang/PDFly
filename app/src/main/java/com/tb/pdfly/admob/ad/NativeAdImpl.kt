package com.tb.pdfly.admob.ad

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.libraries.ads.mobile.sdk.common.AdChoicesPlacement
import com.google.android.libraries.ads.mobile.sdk.common.AdValue
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.LayoutAdNativeLargeBinding
import com.tb.pdfly.databinding.LayoutAdNativeTinyBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter

class NativeAdImpl(override var adPosition: String?, override var adItem: AdConfigItem? = null, override var adLoadTime: Long) : IAd {

    private var mNative: NativeAd? = null

    override fun loadAd(context: Context, onLoaded: (success: Boolean, msg: String?) -> Unit) {

        "------>>$adPosition -- ${adItem?.adType} -- ${adItem?.adId} start load ad------".showLog()

        adItem?.let {

            val adRequest = NativeAdRequest
                .Builder(it.adId, listOf(NativeAd.NativeAdType.NATIVE))
                .setAdChoicesPlacement(AdChoicesPlacement.TOP_RIGHT)
                .build()

            val adCallback = object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(nativeAd: NativeAd) {

                    mNative = nativeAd
                    nativeAd.adEventCallback = object : NativeAdEventCallback {

                        override fun onAdPaid(value: AdValue) {
                            ReportCenter.reportManager.report("pdfly_ad_impression", mapOf("ad_pos_id" to adPosition))
                            onPaidEventListener(value, mNative?.getResponseInfo())
                        }

                        override fun onAdClicked() {
                            //   PostCenter.postCustomEvent("ew_ad_click_n")
                        }
                    }

                    adLoadTime = System.currentTimeMillis()
                    onLoaded(true, null)

                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    onLoaded(false, adError.message)
                }
            }
            NativeAdLoader.load(adRequest, adCallback)
        }
    }

    override fun showAd(activity: BaseActivity<*>, parent: ViewGroup?, nativeType: Int, onClose: () -> Unit) {

        when (nativeType) {
            1 -> {
                mNative?.let { ad ->
                    val binding = LayoutAdNativeLargeBinding.inflate(LayoutInflater.from(activity), parent, false)
                    binding.root.apply {
                        iconView = binding.imageIcon.apply { setImageDrawable(ad.icon?.drawable) }
                        headlineView = binding.textTitle.apply { text = ad.headline ?: "" }
                        bodyView = binding.textBody.apply { text = ad.body ?: "" }
                        callToActionView = binding.btnAction.apply { text = ad.callToAction ?: "" }

                        val mediaView = mediaView?.apply {
                            imageScaleType = ImageView.ScaleType.CENTER_CROP
                            mediaContent = ad.mediaContent
                        }

                        registerNativeAd(ad, mediaView)
                    }
                    parent?.apply {
                        removeAllViews()
                        addView(binding.root)
                    }
                }
            }

            else -> {
                mNative?.let { ad ->
                    val binding = LayoutAdNativeTinyBinding.inflate(LayoutInflater.from(activity), parent, false)
                    binding.root.apply {
                        iconView = binding.imageIcon.apply { setImageDrawable(ad.icon?.drawable) }
                        headlineView = binding.textTitle.apply { text = ad.headline ?: "" }
                        bodyView = binding.textBody.apply { text = ad.body ?: "" }
                        callToActionView = binding.btnAction.apply { text = ad.callToAction ?: "" }

                        val mediaView = mediaView?.apply {
                            imageScaleType = ImageView.ScaleType.CENTER_CROP
                            mediaContent = ad.mediaContent
                        }

                        registerNativeAd(ad, mediaView)
                    }
                    parent?.apply {
                        removeAllViews()
                        addView(binding.root)
                    }
                }
            }

        }
    }

    override fun destroy() {
        mNative?.destroy()
    }
}