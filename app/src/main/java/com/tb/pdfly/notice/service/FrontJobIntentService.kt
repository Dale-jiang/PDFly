package com.tb.pdfly.notice.service

import android.content.Context
import android.content.Intent
import androidx.core.app.SpecialJobService
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showFrontNotice
import com.tb.pdfly.utils.CommonUtils
import com.tb.pdfly.utils.firstCountryCode

class FrontJobIntentService : SpecialJobService() {

    companion object {
        fun start(context: Context) {
            enqueueWork(context, FrontJobIntentService::class.java, 101800, Intent(context, FrontJobIntentService::class.java))
        }
    }

    override fun onHandleWork(intent: Intent) {
        if ("KR" == firstCountryCode && CommonUtils.isSamsungDevice()) return
        mToast("FrontJobIntentService onHandleWork")
        app.showFrontNotice()
    }

}