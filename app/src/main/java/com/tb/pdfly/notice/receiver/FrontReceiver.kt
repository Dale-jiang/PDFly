package com.tb.pdfly.notice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.tb.pdfly.notice.service.FrontNoticeService
import com.tb.pdfly.utils.CommonUtils
import com.tb.pdfly.utils.firstCountryCode
import kotlin.jvm.java

class FrontReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if ("KR" == firstCountryCode && CommonUtils.isSamsungDevice()) return
        runCatching {
            context?.let {
                ContextCompat.startForegroundService(context, Intent(context, FrontNoticeService::class.java))
            }
        }
    }

}