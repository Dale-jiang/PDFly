package com.tb.pdfly.utils

import android.content.Context
import android.content.Context.PRINT_SERVICE
import android.os.ParcelFileDescriptor
import android.print.PrintManager
import com.shockwave.pdfium.PdfiumCore
import com.tb.pdfly.page.adapter.PrintAdapter
import com.tb.pdfly.parameter.FileInfo
import java.io.File

object CommonUtils {

    fun isPdfEncrypted(context: Context, filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        val pdfiumCore = PdfiumCore(context)
        var fd: ParcelFileDescriptor? = null
        return try {
            fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfDocument = pdfiumCore.newDocument(fd)
            pdfiumCore.closeDocument(pdfDocument)
            false
        } catch (e: Exception) {
            val msg = e.message?.lowercase() ?: ""
            msg.contains("password") || msg.contains("encrypted")
        } finally {
            fd?.close()
        }
    }

    fun isPdfPasswordCorrect(context: Context, filePath: String, password: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        val pdfiumCore = PdfiumCore(context)
        var fd: ParcelFileDescriptor? = null
        return try {
            fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfDocument = pdfiumCore.newDocument(fd, password)
            pdfiumCore.closeDocument(pdfDocument)
            true
        } catch (e: Exception) {
            false
        } finally {
            fd?.close()
        }
    }


    fun printPdfFile(context: Context, fileItem: FileInfo) {
        val printManager = context.getSystemService(PRINT_SERVICE) as? PrintManager
        printManager?.print("PDF Print Jobs", PrintAdapter(fileItem), null)
    }


}