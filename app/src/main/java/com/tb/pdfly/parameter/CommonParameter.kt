package com.tb.pdfly.parameter

import com.tb.pdfly.app.MyApp
import com.tb.pdfly.db.AppDatabase

internal lateinit var app: MyApp

internal const val PRIVACY_URL = "https://www.baidu.com"
internal const val EMAIL = ""

internal const val WEB_URL_KEY = "WEB_URL_KEY"

val database by lazy { AppDatabase.getInstance(app) }

val mimetypeMap by lazy {
    mapOf(
        FileType.PDF to listOf("application/pdf"),
        FileType.WORD to listOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.template"
        ),
        FileType.EXCEL to listOf(
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.template"
        ),
        FileType.PPT to listOf(
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.openxmlformats-officedocument.presentationml.template",
            "application/vnd.openxmlformats-officedocument.presentationml.slideshow"
        )
    )
}
