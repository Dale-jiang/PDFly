package com.tb.pdfly.notice

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.mbridge.msdk.out.LoadingActivity
import com.tb.pdfly.R
import com.tb.pdfly.parameter.app
import kotlin.random.Random

object FrontNoticeManager {

    const val KEY_NOTICE_CONTENT = "KEY_NOTICE_CONTENT"
    const val FRONT_NOTICE_ID = 12283
    private const val NOTIFICATION_CHANNEL_ID = "ImportantToolBar"

    @SuppressLint("MissingPermission")
    fun buildNotification(): Notification {

        buildNotificationChannel()

        val builder = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_small_pdf)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSound(null)
            .setOngoing(true)
            .setOnlyAlertOnce(true)

        val bigView = buildRemoteViews(R.layout.layout_front_notice_normal)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val smallView = buildRemoteViews(R.layout.layout_front_notice_tiny)
            builder
                .setCustomContentView(smallView)
                .setCustomBigContentView(bigView)
                .setCustomHeadsUpContentView(bigView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
        } else {
            builder
                .setCustomContentView(bigView)
                .setCustomBigContentView(bigView)
                .setCustomHeadsUpContentView(bigView)
        }

        val notification = builder.build()
        runCatching {
            NotificationManagerCompat.from(app).notify(FRONT_NOTICE_ID, notification)
        }

        return notification
    }

    private fun buildRemoteViews(layoutId: Int): RemoteViews {
        return RemoteViews(app.packageName, layoutId).apply {
            setOnClickPendingIntent(R.id.layout_scan, createPendingIntent(NoticeContent(jumpTYpe = JumpType.CREATE, noticeType = NoticeType.FRONT)))
            setOnClickPendingIntent(R.id.layout_view, createPendingIntent(NoticeContent(jumpTYpe = JumpType.HOME, noticeType = NoticeType.FRONT)))
            setOnClickPendingIntent(R.id.layout_history, createPendingIntent(NoticeContent(jumpTYpe = JumpType.HISTORY, noticeType = NoticeType.FRONT)))
        }
    }

    private fun createPendingIntent(noticeContent: NoticeContent): PendingIntent {
        val intent = Intent(app, LoadingActivity::class.java).apply {
            putExtra(KEY_NOTICE_CONTENT, noticeContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        return PendingIntent.getActivity(app, Random.nextInt(), intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildNotificationChannel() {
        val channel = NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName(NOTIFICATION_CHANNEL_ID)
            .setSound(null, null)
            .setLightsEnabled(false)
            .setVibrationEnabled(false)
            .setShowBadge(false)
            .build()
        NotificationManagerCompat.from(app).createNotificationChannel(channel)
    }

}