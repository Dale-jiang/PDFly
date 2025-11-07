package com.tb.pdfly.notice

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
enum class JumpType : Parcelable {
    HOME, HISTORY, CREATE,
}

@Keep
@Parcelize
enum class NoticeType : Parcelable {
    FRONT, MESSAGE
}

@Keep
@Parcelize
data class NoticeContent(
    var notificationId: Int = 0,
    var message: String = "",
    var btnStr: String = "",
    var triggerType: String = "",
    var jumpType: JumpType,
    var noticeType: NoticeType,
    var isRemote: Boolean = false
) : Parcelable

@Parcelize
@Keep
data class NoticeConfig(
    var open: Int,
    var pdfly_start: Int,
    var pdfly_end: Int,
    var pdfly_im: NoticeConfigItem?,
    var pdfly_lk: NoticeConfigItem?,
) : Parcelable

@Parcelize
@Keep
data class NoticeConfigItem(
    var pdfly_mi: Int,
    var pdfly_up: Int,
) : Parcelable