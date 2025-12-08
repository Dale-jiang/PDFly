package com.tb.pdfly.notice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tb.pdfly.notice.MessageManager
import com.tb.pdfly.notice.service.FrontJobIntentService
import com.tb.pdfly.utils.CommonUtils.ioScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmTaskReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        MessageManager.scheduleNextAlarm()
        ioScope.launch {
            delay(500)
            withContext(Dispatchers.Main){
                MessageManager.showNotice("alarm")
                if (null != context) {
                    FrontJobIntentService.start(context)
                }
            }
        }
    }
}