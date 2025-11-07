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
    var jumpTYpe: JumpType,
    var noticeType: NoticeType,
    var isRemote: Boolean = true
) : Parcelable