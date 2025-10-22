package com.tb.pdfly.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.tb.pdfly.parameter.FileInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface FileInfoDao {

    @Upsert
    suspend fun upsert(info: FileInfo)

    @Delete
    suspend fun delete(info: FileInfo)

    @Query("SELECT * FROM file_info_table WHERE path = :path LIMIT 1")
    suspend fun getFileByPath(path: String): FileInfo?

    @Query("SELECT * FROM file_info_table WHERE recentViewTime > 0 ORDER BY recentViewTime DESC")
    fun getHistoryFiles(): Flow<List<FileInfo>>

    @Query("SELECT * FROM file_info_table WHERE isCollection = 1 ORDER BY dateAdded DESC")
    fun getCollectionFiles(): Flow<List<FileInfo>>

}