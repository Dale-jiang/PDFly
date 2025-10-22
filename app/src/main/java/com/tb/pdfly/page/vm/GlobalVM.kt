package com.tb.pdfly.page.vm

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.database
import com.tb.pdfly.parameter.mimetypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

class GlobalVM : ViewModel() {

    val showNoPermissionLiveData = MutableLiveData<Boolean>()
    val askPermissionLiveData = MutableLiveData<Boolean>()
    val onScanResultLiveData = MutableLiveData<List<FileInfo>>()

    val onHistoryLiveData = MutableLiveData<List<FileInfo>>()
    val onCollectionLiveData = MutableLiveData<List<FileInfo>>()

    fun scanDocs(context: Context) {

        viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {

            val resolver: ContentResolver = context.contentResolver
            val uri: Uri = MediaStore.Files.getContentUri("external")
            val allKnownMimes = mimetypeMap.values.flatten().toSet()
            val placeholders = allKnownMimes.joinToString(",") { "?" }
            val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} IN ($placeholders)"
            val args = allKnownMimes.toTypedArray()
            val sortOrder = "${MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

            val projection = arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.SIZE,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MIME_TYPE
            )

            val result = mutableListOf<FileInfo>()

            val cursor: Cursor? = resolver.query(uri, projection, selection, args, sortOrder)

            cursor?.use {
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)
                val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                val mimeTypeColumn = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

                while (it.moveToNext()) {

                    val path = it.getString(pathColumn) ?: ""
                    val size = it.getLong(sizeColumn)
                    val mimeType = it.getString(mimeTypeColumn) ?: "*/*"
                    val dateAdded = it.getLong(dateAddedColumn) * 1000
                    val file = File(path)
                    if (size <= 0 || !file.exists() || file.isDirectory || !file.canRead() || !file.canWrite()) continue
                    val info = FileInfo(displayName = file.name, path = path, size = size, dateAdded = dateAdded, mimeType = mimeType)
                    result.add(info)
                }
            }

            onScanResultLiveData.postValue(result)

        }
    }

    fun fetchHistoryFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            database.fileInfoDao().getHistoryFiles().collect {
                onHistoryLiveData.postValue(it)
            }
        }
    }

    fun fetchCollectionFiles() {
        viewModelScope.launch(Dispatchers.IO) {
            database.fileInfoDao().getCollectionFiles().collect {
                onCollectionLiveData.postValue(it)
            }
        }
    }


}