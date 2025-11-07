package com.tb.pdfly.notice

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.tb.pdfly.R
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.isGrantedPostNotification
import com.tb.pdfly.utils.applife.HotStartManager
import com.tb.pdfly.utils.applife.HotStartManager.isScreenInteractive
import com.tb.pdfly.utils.noticeLastShowTime
import com.tb.pdfly.utils.timerNoticeCounts
import com.tb.pdfly.utils.timerNoticeLastShowTime
import com.tb.pdfly.utils.unlockNoticeCounts
import com.tb.pdfly.utils.unlockNoticeLastShowTime
import java.util.Calendar
import java.util.concurrent.atomic.AtomicInteger

object MessageManager {

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
        val isSameDay = DateUtils.isToday(noticeLastShowTime)
        if (!isSameDay) {
            timerNoticeCounts = 0
            unlockNoticeCounts = 0
            noticeLastShowTime = System.currentTimeMillis()
        }
        if (type == "time") timerNoticeCounts++ else unlockNoticeCounts++
    }

}