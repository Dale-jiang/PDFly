package com.tb.pdfly.admob.loader

import android.content.Context
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.ad.NativeAdImpl
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.admob.interfaces.IAdLoader
import com.tb.pdfly.page.base.BaseActivity

class NativeAdLoader(override val adPosition: String) : IAdLoader {
    override var onLoaded: ((Boolean) -> Unit)? = null
    override val mAdItems: MutableList<AdConfigItem> = mutableListOf()
    override val adLoadList: MutableList<IAd> = mutableListOf()
    override var isLoading: Boolean = false

    override fun initData(list: MutableList<AdConfigItem>?) {
        list?.let {
            mAdItems.apply {
                clear()
                addAll(it)
                sortByDescending { item -> item.adWeight }
            }
        } ?: run {
            mAdItems.clear()
        }
    }

    fun waitingNativeAd(context: Context, onLoad: (Boolean) -> Unit = {}) = run {
        if (adLoadList.isNotEmpty()) {
            onLoad(true)
            return
        }
        onLoaded = onLoad
        loadAd(context)
    }

    override fun loadAd(context: Context) = run {
        if (mAdItems.isEmpty() || isLoading) return

        adLoadList.firstOrNull()?.apply {
            if (isAdExpire()) adLoadList.remove(this)
        }

        if (adLoadList.isEmpty()) {
            isLoading = true
            doAdLoad(context, 0)
        }
    }

    override fun canShow(activity: BaseActivity<*>): Boolean = run {
        return adLoadList.isNotEmpty() && activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    fun showNativeAd(activity: BaseActivity<*>, parent: ViewGroup, posId: String = adPosition, nativeAdType: Int = 1, callback: (IAd) -> Unit) = run {
        if (adLoadList.isEmpty()) return
        val ad = adLoadList.removeFirstOrNull()
        if (ad is NativeAdImpl) {
            parent.isVisible = true
            ad.adPosition = posId
            ad.showAd(activity, parent, nativeAdType) {}
            callback.invoke(ad)
        }
        onLoaded = {}
        loadAd(activity)
    }

}