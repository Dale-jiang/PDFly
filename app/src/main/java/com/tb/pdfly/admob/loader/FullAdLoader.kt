package com.tb.pdfly.admob.loader

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.ad.FullAd
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.admob.interfaces.IAdLoader
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.showLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullAdLoader(override val adPosition: String) : IAdLoader {

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

    override fun loadAd(context: Context) {
        if (mAdItems.isEmpty() || isLoading) return

        adLoadList.firstOrNull()?.apply {
            if (isAdExpire()) adLoadList.remove(this)
        }

        if (adLoadList.isEmpty()) {
            isLoading = true
            doAdLoad(context, 0)
        }
    }

    override fun canShow(activity: BaseActivity<*>): Boolean {
        return adLoadList.isNotEmpty() && activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
    }

    fun showFullAd(activity: BaseActivity<*>, posId: String = adPosition, showLoading: Boolean = true, onClose: CallBack = {}) {
        if (adLoadList.isEmpty()) {
            onClose()
            return
        }

        activity.lifecycleScope.launch(Dispatchers.Main) {
            val ad = adLoadList.removeFirstOrNull() as? FullAd
            if (ad == null) {
                onClose()
                return@launch
            }

            if (showLoading) {
                runCatching {
                    val dialog = activity.showLoading(R.string.ad_loading)
                    delay(300)
                    dialog?.dismiss()
                }
            }

            ad.showAd(activity, null, onClose)
//            PostCenter.postLogEvent("cc_ad_impression", hashMapOf("ad_pos_id" to posId))
            onLoaded = {}
            loadAd(activity)
        }
    }

}