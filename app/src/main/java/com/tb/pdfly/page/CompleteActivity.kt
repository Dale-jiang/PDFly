package com.tb.pdfly.page

import androidx.activity.addCallback
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.ActivityCompleteBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.read.PDFReadActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class CompleteActivity : BaseActivity<ActivityCompleteBinding>(ActivityCompleteBinding::inflate) {

    companion object {
        const val FILE_INFO = "FILE_INFO"
        const val RESULT_DES = "RESULT_DES"
    }

    private val fileInfo by lazy { intent?.getParcelableExtra<FileInfo>(FILE_INFO) }
    private val resultDesc by lazy { intent?.getStringExtra(RESULT_DES) }


    override fun initView() {

        binding.apply {

            onBackPressedDispatcher.addCallback {
                finish()
            }
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }


            textDes.text = resultDesc ?: getString(R.string.completed)
            dialogName.text = fileInfo?.displayName ?: ""
            dialogDesc.text = fileInfo?.path ?: ""

            btnOpen.setOnClickListener {
//                lifecycleScope.launch(Dispatchers.IO) {
//                    runCatching {
//                        val findItem = database.fileInfoDao().getFileByPath(fileInfo?.path ?: "") ?: fileInfo
//                        findItem?.recentViewTime = System.currentTimeMillis()
//                        findItem?.let { database.fileInfoDao().upsert(findItem) }
//                    }
//                }
                showViewAd {
                    toActivity<PDFReadActivity> {
                        putExtra(PDFReadActivity.FILE_INFO, fileInfo)
                    }
                }
                finish()
            }

        }

        showNatAd()
    }


    private fun showViewAd(callBack: CallBack) {
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_scan_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyScanInt
            if (ad.canShow(this@CompleteActivity)) {
                ad.showFullAd(this@CompleteActivity, "pdfly_scan_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@CompleteActivity)
                callBack()
            }
        }
    }

    private var ad: IAd? = null
    private fun showNatAd() {
        if (AdCenter.adNoNeededShow()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_result_nat"))
        val nAd = AdCenter.pdflyScanNat
        nAd.waitingNativeAd(this@CompleteActivity) {
            if (nAd.canShow(this@CompleteActivity)) {
                binding.adContainer.apply {
                    ad?.destroy()
                    nAd.showNativeAd(this@CompleteActivity, this, "pdfly_result_nat") {
                        ad = it
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ad?.destroy()
    }

}