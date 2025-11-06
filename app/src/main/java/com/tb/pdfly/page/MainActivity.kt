package com.tb.pdfly.page

import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.ActivityMainBinding
import com.tb.pdfly.page.base.BaseFilePermissionActivity
import com.tb.pdfly.page.dialog.RenameDialog
import com.tb.pdfly.page.fragments.CollectionFragment
import com.tb.pdfly.page.fragments.HistoryFragment
import com.tb.pdfly.page.fragments.HomeFragment
import com.tb.pdfly.page.fragments.SettingsFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.database
import com.tb.pdfly.parameter.hasStoragePermission
import com.tb.pdfly.parameter.notifyPdfUpdate
import com.tb.pdfly.parameter.showLoading
import com.tb.pdfly.parameter.showRatingDialog
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.applife.HotStartManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : BaseFilePermissionActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val createLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        HotStartManager.navigateToSettingPage(false)
        if (result.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            result?.pdf?.let { pdf ->
                showCreateAd {
                    val pdfUri = pdf.uri
                    onCreatedSucceed(pdfUri)
                }
            } ?: Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
        } else if (result.resultCode == RESULT_CANCELED) {
            Toast.makeText(this, getString(R.string.create_cancelled), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
        }
    }

    private val viewModel by viewModels<GlobalVM>()

    override fun onResume() {
        super.onResume()
        showRatingDialog()
    }

    override fun initView() {

        AdCenter.pdflyScanInt.loadAd(this)
        AdCenter.pdflyBackInt.loadAd(this)
        AdCenter.pdflyMainNat.loadAd(this)

        if (hasStoragePermission()) {
            viewModel.showNoPermissionLiveData.postValue(false)
            viewModel.scanDocs(this)
        } else {
            viewModel.showNoPermissionLiveData.postValue(true)
        }

        initViewPager()

        binding.btnCreate.setOnClickListener {
            goCreatePdf()
        }

        viewModel.askPermissionLiveData.observe(this) {
            checkStoragePermission()
        }

        handleOnBackPressed()
    }

    override fun onStoragePermissionGranted() {
        viewModel.showNoPermissionLiveData.postValue(false)
        viewModel.scanDocs(this)
    }

    private fun initViewPager() {
        setBottomButton(0)
        val fragments = listOf(HomeFragment(), HistoryFragment(), CollectionFragment(), SettingsFragment())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = fragments.size
        binding.viewPager.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setBottomButton(position)
            }
        })

        binding.btnHome.setOnClickListener { binding.viewPager.setCurrentItem(0, false) }
        binding.btnHistory.setOnClickListener { binding.viewPager.setCurrentItem(1, false) }
        binding.btnBookmarks.setOnClickListener { binding.viewPager.setCurrentItem(2, false) }
        binding.btnMine.setOnClickListener { binding.viewPager.setCurrentItem(3, false) }
    }


    private fun setBottomButton(position: Int) {
        binding.apply {

            when (position) {
                0 -> {
                    btnHome.isSelected = true
                    homeIndicator.isVisible = true
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                1 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = true
                    historyIndicator.isVisible = true
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                2 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = true
                    bookmarksIndicator.isVisible = true
                    btnMine.isSelected = false
                    mineIndicator.isVisible = false
                }

                3 -> {
                    btnHome.isSelected = false
                    homeIndicator.isVisible = false
                    btnHistory.isSelected = false
                    historyIndicator.isVisible = false
                    btnBookmarks.isSelected = false
                    bookmarksIndicator.isVisible = false
                    btnMine.isSelected = true
                    mineIndicator.isVisible = true
                }
            }
        }
    }

    private fun goCreatePdf() {
        if (hasStoragePermission().not()) {
            viewModel.askPermissionLiveData.postValue(true)
            return
        }
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
            .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
            .build()
        val scanner = GmsDocumentScanning.getClient(options)
        scanner.getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                HotStartManager.navigateToSettingPage(true)
                createLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
            }
    }

    private fun onCreatedSucceed(uri: Uri) {
        RenameDialog {
            if (it.isEmpty().not()) {
                lifecycleScope.launch {

                    val loadingDialog = showLoading(R.string.creating)
                    val startTime = System.currentTimeMillis()
                    val result = savePDFFile(uri, it)

                    val delayTime = 2000L + startTime - System.currentTimeMillis()
                    if (delayTime > 0) delay(delayTime)

                    if (null != result) {
                        val fileItem = FileInfo(
                            displayName = result.name,
                            path = result.path,
                            mimeType = "application/pdf",
                            size = result.length(),
                            dateAdded = result.lastModified(),
                        )
                        database.fileInfoDao().upsert(fileItem)
                        viewModel.scanDocs(this@MainActivity)
                        toActivity<CompleteActivity> {
                            putExtra(CompleteActivity.FILE_INFO, fileItem)
                            putExtra(CompleteActivity.RESULT_DES, getString(R.string.created_successfully))
                        }
                    } else {
                        Toast.makeText(this@MainActivity, getString(R.string.create_failed), Toast.LENGTH_LONG).show()
                    }

                    loadingDialog?.dismiss()
                }
            } else {
                runCatching {
                    contentResolver.delete(uri, null, null)
                }
            }
        }.show(supportFragmentManager, "DialogRenamePDF")
    }

    private suspend fun savePDFFile(uri: Uri, name: String): File? = withContext(Dispatchers.IO) {
        val path = uri.path ?: return@withContext null
        val inputFile = File(path)
        val targetDir = File(
            Environment.getExternalStorageDirectory(), "${Environment.DIRECTORY_DOCUMENTS}${File.separator}pdf${File.separator}pdfly"
        ).apply {
            if (!exists()) mkdirs()
        }
        val targetFile = File(targetDir, "$name.pdf")
        try {
            inputFile.copyTo(targetFile, true)
            inputFile.delete()
            notifyPdfUpdate(targetFile)
            targetFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback {
            if (!AdCenter.adNoNeededShow()) {
                ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "exit_launch"))
            }
            if (AdCenter.pdflyLaunch.canShow(this@MainActivity)) {
                showExitAd {
                    moveTaskToBack(true)
                }
            } else moveTaskToBack(true)
        }
    }

    private fun showExitAd(callBack: CallBack) {

        if (AdCenter.adNoNeededShow()) {
            callBack()
            return
        }

        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyLaunch
            if (ad.canShow(this@MainActivity)) {
                ad.showFullAd(this@MainActivity, "exit_launch", showLoading = false) { callBack() }
            } else {
                ad.loadAd(this@MainActivity)
                callBack()
            }
        }
    }

    private fun showCreateAd(callBack: CallBack) {
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_scan_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyScanInt
            if (ad.canShow(this@MainActivity)) {
                ad.showFullAd(this@MainActivity, "pdfly_scan_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@MainActivity)
                callBack()
            }
        }
    }

}