package com.tb.pdfly.page.read

import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import com.ahmer.pdfium.PdfiumCore
import com.ahmer.pdfviewer.scroll.DefaultScrollHandle
import com.ahmer.pdfviewer.util.FitPolicy
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityPdfReadBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.dialog.PasswordDialog
import com.tb.pdfly.parameter.FileInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Suppress("DEPRECATION")
class PDFReadActivity : BaseActivity<ActivityPdfReadBinding>(ActivityPdfReadBinding::inflate) {

    companion object {
        const val FILE_INFO = "FILE_INFO"
    }

    private val fileInfo by lazy { intent?.getParcelableExtra<FileInfo>(FILE_INFO) }


    override fun initView() {

        if (fileInfo == null || !File(fileInfo!!.path).exists()) {
            Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
            finish()
            return
        }
        lifecycleScope.launch {
            fileInfo?.apply {
                binding.title.text = displayName
                val needPass = isPdfEncrypted(this.path)
                delay(500)
                if (needPass) {
                    PasswordDialog(path) { pass ->

                        if (pass.isBlank()) {
                            finish()
                            return@PasswordDialog
                        }

                        binding.pdfView.fromFile(File(path))
                            .password(pass)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubleTap(true)
                            .enableAnnotationRendering(true)
                            .scrollHandle(DefaultScrollHandle(this@PDFReadActivity))
                            .enableAntialiasing(true)
                            .spacing(3)
                            .autoSpacing(false)
                            .pageFitPolicy(FitPolicy.WIDTH)
                            .fitEachPage(false).pageSnap(false)
                            .pageFling(false)
                            .pageSnap(false)
                            .nightMode(false)
                            .load()

                    }.show(supportFragmentManager, "pass")
                } else {
                    binding.pdfView.fromFile(File(path))
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubleTap(true)
                        .enableAnnotationRendering(true)
                        .scrollHandle(DefaultScrollHandle(this@PDFReadActivity))
                        .enableAntialiasing(true)
                        .spacing(3)
                        .autoSpacing(false)
                        .pageFitPolicy(FitPolicy.WIDTH)
                        .fitEachPage(false).pageSnap(false)
                        .pageFling(false)
                        .pageSnap(false)
                        .nightMode(false)
                        .load()
                }
            }
        }

        onBackPressedDispatcher.addCallback {
            finish()
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    fun isPdfEncrypted(filePath: String): Boolean {
        val file = File(filePath)
        if (!file.exists()) return false

        val pdfiumCore = PdfiumCore(this)
        var fd: ParcelFileDescriptor? = null
        return try {
            fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfiumCore.newDocument(fd)
            pdfiumCore.close()
            false
        } catch (e: Exception) {
            val msg = e.message?.lowercase() ?: ""
            msg.contains("password") || msg.contains("encrypted")
        } finally {
            fd?.close()
        }
    }

}