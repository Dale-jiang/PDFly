package com.tb.pdfly.admob.interfaces

import android.app.Activity
import android.content.Context
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.ad.FullAdImpl
import com.tb.pdfly.admob.ad.NativeAdImpl
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog

interface IAdLoader {

    val adPosition: String
    var onLoaded: ((Boolean) -> Unit)?
    val mAdItems: MutableList<AdConfigItem>
    val adLoadList: MutableList<IAd>
    var isLoading: Boolean

    fun initData(list: MutableList<AdConfigItem>?)
    fun loadAd(context: Context = app)
    fun canShow(activity: BaseActivity<*>): Boolean

    private fun createAd(item: AdConfigItem): IAd? {
        return when (item.adPlatform) {
            "admob" -> when (item.adType) {
                "op", "int" -> FullAdImpl(adPosition, adItem = item, System.currentTimeMillis())
                "nat" -> NativeAdImpl(adPosition, adItem = item, System.currentTimeMillis())
                else -> null
            }

            else -> null
        }
    }

    fun doAdLoad(activity: Activity, startIndex: Int) {

        var currentIndex = startIndex

        fun tryLoadNext() {
            if (activity.isFinishing || activity.isDestroyed) {
                isLoading = false
                onLoaded?.invoke(false)
                return
            }

            val item = mAdItems.getOrNull(currentIndex)
            if (item == null) {
                currentIndex = 0
                tryLoadNext()
                return
            }

            val baseAd = createAd(item)
            if (baseAd == null) {
                currentIndex = (currentIndex + 1) % mAdItems.size
                tryLoadNext()
                return
            }

            baseAd.loadAd(activity) { success, msg ->

                if (activity.isFinishing || activity.isDestroyed) {
                    isLoading = false
                    onLoaded?.invoke(false)
                    return@loadAd
                }

                if (success) {
                    "$adPosition ${item.adType} - ${item.adId} load success".showLog()
                    isLoading = false
                    onAdLoaded(baseAd)
                } else {
                    "$adPosition ${item.adType} - ${item.adId} load failed: $msg".showLog()

                    currentIndex = (currentIndex + 1) % mAdItems.size

                    tryLoadNext()
                }
            }
        }

        tryLoadNext()
    }


    fun doAdLoad(context: Context, startIndex: Int) {

        fun loadNextAd(index: Int) {
            val item = mAdItems.getOrNull(index)
            if (item == null) {
                onLoaded?.invoke(false)
                isLoading = false
                return
            }

            val baseAd = createAd(item)
            if (baseAd == null) {
                loadNextAd(index + 1)
                return
            }

            baseAd.loadAd(context) { success, msg ->
                if (success) {
                    "$adPosition ${item.adType} - ${item.adId} load success".showLog()
                    onAdLoaded(baseAd)
                } else {
                    "$adPosition ${item.adType} - ${item.adId} load failed: $msg".showLog()
                    loadNextAd(index + 1)
                }
            }
        }

        loadNextAd(startIndex)
    }

    private fun onAdLoaded(ad: IAd) {
        runCatching {
            adLoadList.add(ad)
            adLoadList.sortByDescending { it.adItem!!.adWeight }
            isLoading = false
            onLoaded?.invoke(true)
        }
    }
}