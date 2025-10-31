package com.tb.pdfly.admob.interfaces

import android.content.Context
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.ad.FullAd
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
    fun canShow(activity: BaseActivity<*>): Boolean
    fun loadAd(context: Context = app)

    private fun createAd(item: AdConfigItem): IAd? {
        return when (item.adPlatform) {
            "admob" -> when (item.adType) {
                "op", "int" -> FullAd(adPosition, adItem = item, System.currentTimeMillis())
                // "nat" -> PBNativeAd(adPosition, adItem = item, System.currentTimeMillis())
                else -> null
            }

            else -> null
        }
    }

    fun doAdLoad(context: Context, startIndex: Int) {

        fun loadNextAd(index: Int) {
            val item = mAdItems.getOrNull(index) ?: if (adPosition == "pdfly_launch") mAdItems.getOrNull(0) else null
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