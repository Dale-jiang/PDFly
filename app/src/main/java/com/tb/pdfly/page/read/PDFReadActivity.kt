package com.tb.pdfly.page.read

import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.ActivityPdfReadBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.dialog.PasswordDialog
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.database
import com.tb.pdfly.parameter.showFileDetailsDialog
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CommonUtils.isPdfEncrypted
import kotlinx.coroutines.Dispatchers
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
                val needPass = isPdfEncrypted(this@PDFReadActivity, this.path)
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
                            .enableDoubletap(true)
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

                        saveHistory()

                    }.show(supportFragmentManager, "pass")
                } else {
                    binding.pdfView.fromFile(File(path))
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
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

                    saveHistory()
                }
            }
        }

        onBackPressedDispatcher.addCallback {
            showBackAd {
                finish()
            }
        }
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.btnMore.setOnClickListener {
            showFileDetailsDialog(fileInfo!!, true)
        }
    }


    private fun saveHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            runCatching {
                val findItem = database.fileInfoDao().getFileByPath(fileInfo?.path ?: "") ?: fileInfo
                findItem?.recentViewTime = System.currentTimeMillis()
                findItem?.let { database.fileInfoDao().upsert(findItem) }
            }
        }
    }

    private fun showBackAd(callBack: CallBack) {
        if (AdCenter.adNoNeededShow()) {
            callBack()
            return
        }
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_back_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyBackInt
            if (ad.canShow(this@PDFReadActivity)) {
                ad.showFullAd(this@PDFReadActivity, "pdfly_back_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@PDFReadActivity)
                callBack()
            }
        }
    }

}