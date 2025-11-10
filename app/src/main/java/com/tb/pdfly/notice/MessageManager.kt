package com.tb.pdfly.notice

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.format.DateUtils
import android.widget.RemoteViews
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DecoratedCustomViewStyle
import androidx.core.app.NotificationManagerCompat
import com.tb.pdfly.R
import com.tb.pdfly.notice.FrontNoticeManager.KEY_NOTICE_CONTENT
import com.tb.pdfly.page.guide.FirstLoadingActivity
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.isGrantedPostNotification
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.applife.HotStartManager
import com.tb.pdfly.utils.applife.HotStartManager.isScreenInteractive
import com.tb.pdfly.utils.noticeLastShowTime
import com.tb.pdfly.utils.timerNoticeCounts
import com.tb.pdfly.utils.timerNoticeLastShowTime
import com.tb.pdfly.utils.unlockNoticeCounts
import com.tb.pdfly.utils.unlockNoticeLastShowTime
import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

object MessageManager {

    private const val NOTIFICATION_CHANNEL_ID = "MAIN_IMPORTANT_MESSAGE"
    var remoteMessageList: List<List<NoticeContent>> = listOf()
    var remoteNoticeConfig: NoticeConfig? = null
    private val currentGroupIndexAtomic = AtomicInteger(0)

    val groupText1: List<NoticeContent>
        get() = listOf(
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_1), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_2), btnStr = app.getString(R.string.view), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_3), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_4), btnStr = app.getString(R.string.view), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_5), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_6), btnStr = app.getString(R.string.view), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_7), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_8), btnStr = app.getString(R.string.view), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_9), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11012, message = app.getString(R.string.notice_text_10), btnStr = app.getString(R.string.view), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
        )

    val groupText2: List<NoticeContent>
        get() = listOf(
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_11),
                btnStr = app.getString(R.string.view),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_12),
                btnStr = app.getString(R.string.open),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11013, message = app.getString(R.string.notice_text_13), btnStr = app.getString(R.string.see), jumpType = JumpType.HISTORY, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_14),
                btnStr = app.getString(R.string.view),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_15),
                btnStr = app.getString(R.string.open),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_16),
                btnStr = app.getString(R.string.view),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_17),
                btnStr = app.getString(R.string.open),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11013, message = app.getString(R.string.notice_text_18), btnStr = app.getString(R.string.see), jumpType = JumpType.HISTORY, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_19),
                btnStr = app.getString(R.string.view),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11013,
                message = app.getString(R.string.notice_text_20),
                btnStr = app.getString(R.string.open),
                jumpType = JumpType.HISTORY,
                noticeType = NoticeType.MESSAGE
            ),
        )

    val groupText3: List<NoticeContent>
        get() = listOf(
            NoticeContent(notificationId = 11014, message = app.getString(R.string.notice_text_21), btnStr = app.getString(R.string.scan), jumpType = JumpType.CREATE, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11014,
                message = app.getString(R.string.notice_text_22),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11014, message = app.getString(R.string.notice_text_23), btnStr = app.getString(R.string.scan), jumpType = JumpType.CREATE, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11014,
                message = app.getString(R.string.notice_text_24),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11014, message = app.getString(R.string.notice_text_25), btnStr = app.getString(R.string.scan), jumpType = JumpType.CREATE, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11014,
                message = app.getString(R.string.notice_text_26),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11014, message = app.getString(R.string.notice_text_27), btnStr = app.getString(R.string.scan), jumpType = JumpType.CREATE, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11014,
                message = app.getString(R.string.notice_text_28),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(notificationId = 11014, message = app.getString(R.string.notice_text_29), btnStr = app.getString(R.string.scan), jumpType = JumpType.CREATE, noticeType = NoticeType.MESSAGE),
            NoticeContent(
                notificationId = 11014,
                message = app.getString(R.string.notice_text_30),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
        )

    val groupText4: List<NoticeContent>
        get() = listOf(
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_31),
                btnStr = app.getString(R.string.create),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_32),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_33),
                btnStr = app.getString(R.string.create),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_34),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_35),
                btnStr = app.getString(R.string.create),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_36),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_37),
                btnStr = app.getString(R.string.create),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_38),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_39),
                btnStr = app.getString(R.string.create),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
            NoticeContent(
                notificationId = 11015,
                message = app.getString(R.string.notice_text_40),
                btnStr = app.getString(R.string.start),
                jumpType = JumpType.CREATE,
                noticeType = NoticeType.MESSAGE
            ),
        )

    val groupText5: List<NoticeContent>
        get() = listOf(
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_41), btnStr = app.getString(R.string.go), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_42), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_43), btnStr = app.getString(R.string.go), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_44), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_45), btnStr = app.getString(R.string.go), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_46), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_47), btnStr = app.getString(R.string.go), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_48), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_49), btnStr = app.getString(R.string.go), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
            NoticeContent(notificationId = 11015, message = app.getString(R.string.notice_text_50), btnStr = app.getString(R.string.open), jumpType = JumpType.HOME, noticeType = NoticeType.MESSAGE),
        )


    @SuppressLint("MissingPermission")
    fun showNotice(type: String) {
        if (!canShow(type)) return
        val noticeItem = getNoticeItem() ?: return
        noticeItem.triggerType = type

        ReportCenter.reportManager.report("notification_trigger_count", hashMapOf("list" to (if (type == "time") "times" else "lock")))

        buildNotificationChannel()

        val builder = NotificationCompat.Builder(app, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_small_pdf)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPendingIntent(noticeItem))
            .setAutoCancel(true)
            .setGroupSummary(false)
            .setGroup("important_message")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val tiny = buildRemoteViews(noticeItem, R.layout.layout_message_tiny)
            val large = buildRemoteViews(noticeItem, R.layout.layout_message_large)
            builder.setCustomContentView(tiny).setCustomHeadsUpContentView(tiny).setCustomBigContentView(large)
            builder.setStyle(DecoratedCustomViewStyle())
        } else {
            val mid = buildRemoteViews(noticeItem, R.layout.layout_message_normal)
            val large = buildRemoteViews(noticeItem, R.layout.layout_message_large)
            builder.setCustomContentView(mid).setCustomHeadsUpContentView(mid).setCustomBigContentView(large)
        }
        runCatching {
            NotificationManagerCompat.from(app).notify(noticeItem.notificationId, builder.build())
            updateNoticeShowCounts(type)
        }

    }

    private fun buildRemoteViews(noticeContent: NoticeContent, layoutId: Int): RemoteViews {

        val drawableId = when (noticeContent.jumpType) {
            JumpType.HOME -> R.drawable.image_message_view
            JumpType.HISTORY -> R.drawable.image_message_history
            JumpType.CREATE -> R.drawable.image_message_create
        }

        return RemoteViews(app.packageName, layoutId).also {
            if (layoutId == R.layout.layout_message_large) {
                it.setImageViewResource(R.id.image_message, drawableId)
            }
            it.setTextViewText(R.id.text_message, Html.fromHtml(noticeContent.message))
            it.setTextViewText(R.id.text_action, noticeContent.btnStr)
        }
    }

    private fun createPendingIntent(noticeContent: NoticeContent): PendingIntent {
        val intent = Intent(app, FirstLoadingActivity::class.java).apply {
            putExtra(KEY_NOTICE_CONTENT, noticeContent)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        return PendingIntent.getActivity(app, Random.nextInt(), intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun buildNotificationChannel() = run {
        NotificationManagerCompat.from(app).createNotificationChannel(
            NotificationChannelCompat.Builder(NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_MAX)
                .setLightsEnabled(true)
                .setVibrationEnabled(true)
                .setShowBadge(true)
                .setName(NOTIFICATION_CHANNEL_ID)
                .build()
        )
    }

    private fun getNoticeItem(): NoticeContent? {
        val groups = remoteMessageList.ifEmpty {
            listOf(groupText1, groupText2, groupText3, groupText4, groupText5)
        }
        if (groups.isEmpty()) return null

        val index = currentGroupIndexAtomic.getAndIncrement() % groups.size
        return groups[index].randomOrNull()
    }

    private fun canShow(type: String): Boolean {

        if (HotStartManager.isAppForeground()) return false
        if (remoteNoticeConfig == null) return false
        if (0 == remoteNoticeConfig!!.open) return false
        if (app.isGrantedPostNotification().not()) return false

        val item = if (type == "time") remoteNoticeConfig!!.pdfly_im else remoteNoticeConfig!!.pdfly_lk
        if (item == null) return false

        val lastShowTime = if (type == "time") timerNoticeLastShowTime else unlockNoticeLastShowTime
        if (item.pdfly_mi != 0 && (System.currentTimeMillis() - lastShowTime) < (item.pdfly_mi * 60000L)) return false

        if (item.pdfly_up != 0 && getNoticeShowCounts(type) >= item.pdfly_up) return false

        if (!judgeTimeCan(type)) return false

        return true
    }

    private fun judgeTimeCan(type: String): Boolean {

        val config = remoteNoticeConfig ?: return false
        val blockStart = config.pdfly_start
        val blockEnd = config.pdfly_end

        if (type != "time" || blockStart == blockEnd || isScreenInteractive()) {
            return true
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        val isInBlockTime = if (blockEnd > blockStart) {
            currentHour in blockStart until blockEnd
        } else {
            currentHour >= blockStart || currentHour in 0 until blockEnd
        }

        return !isInBlockTime
    }

    private fun getNoticeShowCounts(type: String): Int {
        val isSameDay = DateUtils.isToday(noticeLastShowTime)
        if (!isSameDay) {
            timerNoticeCounts = 0
            unlockNoticeCounts = 0
            noticeLastShowTime = System.currentTimeMillis()
        }
        return if (type == "time") timerNoticeCounts else unlockNoticeCounts
    }

    private fun updateNoticeShowCounts(type: String) {
        if (type == "time") {
            timerNoticeLastShowTime = System.currentTimeMillis()
        } else {
            unlockNoticeLastShowTime = System.currentTimeMillis()
        }
        val isSameDay = DateUtils.isToday(noticeLastShowTime)
        if (!isSameDay) {
            timerNoticeCounts = 0
            unlockNoticeCounts = 0
            noticeLastShowTime = System.currentTimeMillis()
        }
        if (type == "time") timerNoticeCounts++ else unlockNoticeCounts++
    }

}