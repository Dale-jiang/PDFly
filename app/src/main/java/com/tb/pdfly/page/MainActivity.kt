package com.tb.pdfly.page

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdValue
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.AdmobRevenueManager.onBannerPaidEventListener
import com.tb.pdfly.databinding.ActivityMainBinding
import com.tb.pdfly.notice.FrontNoticeManager.KEY_NOTICE_CONTENT
import com.tb.pdfly.notice.JumpType
import com.tb.pdfly.notice.NoticeContent
import com.tb.pdfly.page.base.BaseFilePermissionActivity
import com.tb.pdfly.page.dialog.RenameDialog
import com.tb.pdfly.page.fragments.CollectionFragment
import com.tb.pdfly.page.fragments.HistoryFragment
import com.tb.pdfly.page.fragments.HomeFragment
import com.tb.pdfly.page.fragments.SettingsFragment
import com.tb.pdfly.page.vm.GlobalVM
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.FileInfo
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.database
import com.tb.pdfly.parameter.getScreenWidth
import com.tb.pdfly.parameter.hasStoragePermission
import com.tb.pdfly.parameter.isGrantedPostNotification
import com.tb.pdfly.parameter.notifyPdfUpdate
import com.tb.pdfly.parameter.showLoading
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.parameter.showRatingDialog
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.applife.HotStartManager
import com.tb.pdfly.utils.isRequestNotice
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

    private val noticeResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        showRatingDialog()
        if (isGrantedPostNotification()) {

        }
    }

    private val noticeSetLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        showRatingDialog()
        HotStartManager.navigateToSettingPage(false)
        if (isGrantedPostNotification()) {

        }
    }

    private val viewModel by viewModels<GlobalVM>()
    private var isLoadingBannerAd = false
    private var bannerAd: BannerAd? = null
    private var isMainShowNoticeDialog = false

    private val noticeContent by lazy { intent?.getParcelableExtra<NoticeContent>(KEY_NOTICE_CONTENT) }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            delay(1000)
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                loadBanner()
            }
        }
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
            ReportCenter.reportManager.report("scan_image_to_pdf_click_count")
            goCreatePdf()
        }

        viewModel.askPermissionLiveData.observe(this) {
            checkStoragePermission()
        }

        handleOnBackPressed()
        if (noticeContent != null) {
            when (noticeContent!!.jumpType) {
                JumpType.HOME -> {}
                JumpType.HISTORY -> {
                    binding.btnHistory.performClick()
                }

                JumpType.CREATE -> goCreatePdf()

            }
        } else {
            showNoticeGuideIfCan()
        }
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


    private fun showNoticeGuideIfCan(): Boolean = run {
        if (isGrantedPostNotification()) return false
        if (isMainShowNoticeDialog) return false
        isMainShowNoticeDialog = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isRequestNotice) {
                isRequestNotice = true
                noticeResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                noticeResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showPermissionNoticeDialog()
            }
        } else {
            showPermissionNoticeDialog()
        }
        return true
    }

    private fun showPermissionNoticeDialog() = run {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { it.putExtra(Settings.EXTRA_APP_PACKAGE, app.packageName) }
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.notification_permission))
            .setMessage(getString(R.string.grant_access_to_get_important_notifications))
            .setPositiveButton(getString(R.string.grant)) { dialog, _ ->
                dialog.dismiss()
                HotStartManager.navigateToSettingPage(true)
                noticeSetLauncher.launch(intent)
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                showRatingDialog()
            }
            .show()
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


    private fun loadBanner() {
        if (AdCenter.bannerId.isEmpty()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "pdfly_main_ban"))
        if (isLoadingBannerAd) return
        isLoadingBannerAd = true
        val extras = Bundle()
        extras.putString("collapsible", "bottom")
        val a = getScreenWidth().toInt()
        val adRequest =
            BannerAdRequest
                .Builder(AdCenter.bannerId, AdSize.BANNER)
                .setGoogleExtrasBundle(extras)
                .build()
        BannerAd.load(adRequest, object : AdLoadCallback<BannerAd> {
            override fun onAdLoaded(ad: BannerAd) {
                "The last loaded banner is ${if (ad.isCollapsible()) "" else "not "}collapsible.---${a}".showLog()
                isLoadingBannerAd = false
                bannerAd?.destroy()
                bannerAd = ad
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.adContainer.removeAllViews()
                    binding.adContainer.addView(ad.getView(this@MainActivity))
                }

                ad.adEventCallback = object : BannerAdEventCallback {
                    override fun onAdImpression() {
                        ReportCenter.reportManager.report("pdfly_ad_impression", hashMapOf("ad_pos_id" to "pdfly_main_ban"))
                    }

                    override fun onAdPaid(value: AdValue) {
                        onBannerPaidEventListener(value, bannerAd?.getResponseInfo())
                    }
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                bannerAd = null
                isLoadingBannerAd = false
            }
        }
        )
    }

}