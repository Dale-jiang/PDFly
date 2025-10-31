package com.tb.pdfly.admob.loader

import android.content.Context
import androidx.lifecycle.Lifecycle
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.admob.interfaces.IAdLoader
import com.tb.pdfly.page.base.BaseActivity

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

}