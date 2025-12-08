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
    var pdfly_al: NoticeConfigItem?,
) : Parcelable

@Parcelize
@Keep
data class NoticeConfigItem(
    var pdfly_mi: Int,
    var pdfly_up: Int,
) : Parcelable

val testJson = """
    {
    	"open": 1,
    	"pdfly_start": 0,
    	"pdfly_end": 0,
    	"pdfly_im": {
    		"pdfly_mi": 20,
    		"pdfly_up": 30
    	},
    	"pdfly_lk": {
    		"pdfly_mi": 0,
    		"pdfly_up": 30
    	},
    	"pdfly_al": {
    		"pdfly_mi": 1,
    		"pdfly_up": 100
    	}
    }
""".trimIndent()