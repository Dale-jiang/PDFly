package com.tb.pdfly.notice.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.tb.pdfly.notice.FrontNoticeManager

class FrontNoticeService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isFrontServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startNotificationMain()
        return START_STICKY
    }

    private fun startNotificationMain() = run {
        runCatching {
            val notification = FrontNoticeManager.buildNotification()
            startForeground(FrontNoticeManager.FRONT_NOTICE_ID, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isFrontServiceRunning = false
    }

    companion object {
        var isFrontServiceRunning = false
    }
}