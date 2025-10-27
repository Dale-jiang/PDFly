package com.tb.pdfly.parameter

import androidx.lifecycle.MutableLiveData
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.app.MyApp
import com.tb.pdfly.db.AppDatabase

internal lateinit var app: MyApp

var PRIVACY_URL = if (BuildConfig.DEBUG) "https://www.bing.com" else "https://sites.google.com/view/pdfly--privacypolicy/home"
internal const val EMAIL = ""

internal const val WEB_URL_KEY = "WEB_URL_KEY"

val database by lazy { AppDatabase.getInstance(app) }
val changeNameLiveData = MutableLiveData<Pair<String, String>>()
val fileDeleteLiveData = MutableLiveData<String>()

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
