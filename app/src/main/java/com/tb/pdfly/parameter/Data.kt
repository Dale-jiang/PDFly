package com.tb.pdfly.parameter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileInfo(
    var displayName: String = "",
    var path: String = "",
    val mimeType: String = "",
    val size: Long = 0L,
    val dateAdded: Long = 0L,
    var recentViewTime: Long = 0L,
    var isCollection: Boolean = false,
    var collectionTime: Long = 0L,
) : Parcelable