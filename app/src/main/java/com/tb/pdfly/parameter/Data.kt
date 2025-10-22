package com.tb.pdfly.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import com.tb.pdfly.R
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
) : Parcelable {
    fun getFileType(): FileType? {
        return mimetypeMap.entries.find { mimeType in it.value }?.key
    }
}

@Keep
enum class TabType {
    HOME, HISTORY, COLLECTION
}

@Keep
enum class FileType(val iconId: Int) {
    ALL(-1),
    PDF(R.drawable.image_first_loading_logo),
    WORD(R.drawable.image_first_loading_logo),
    EXCEL(R.drawable.image_first_loading_logo),
    PPT(R.drawable.image_first_loading_logo),
}