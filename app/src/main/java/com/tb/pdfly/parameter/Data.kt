package com.tb.pdfly.parameter

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tb.pdfly.R
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "file_info_table")
data class FileInfo(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
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
    ALL(R.drawable.image_file_other),
    PDF(R.drawable.image_file_pdf),
    WORD(R.drawable.image_file_word),
    EXCEL(R.drawable.image_file_excel),
    PPT(R.drawable.image_file_ppt),
}