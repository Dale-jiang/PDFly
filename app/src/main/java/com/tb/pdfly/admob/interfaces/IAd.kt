package com.tb.pdfly.admob.interfaces

import android.content.Context
import android.view.ViewGroup
import com.tb.pdfly.admob.AdConfigItem
import com.tb.pdfly.page.base.BaseActivity

interface IAd {

    var adPosition: String?
    var adItem: AdConfigItem?
    var adLoadTime: Long

    fun loadAd(context: Context, onLoaded: (success: Boolean, msg: String?) -> Unit)
    fun showAd(activity: BaseActivity<*>, parent: ViewGroup?, nativeType: Int, onClose: () -> Unit)
    fun destroy()

    fun isAdExpire(): Boolean {
        return adItem?.run {
            System.currentTimeMillis() - adLoadTime >= this.adExpireTime * 1000L
        } ?: true
    }
}