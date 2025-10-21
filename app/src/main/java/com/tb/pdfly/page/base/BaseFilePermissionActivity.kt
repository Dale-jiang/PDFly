package com.tb.pdfly.page.base

import android.Manifest
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.viewbinding.ViewBinding
import com.tb.pdfly.R
import com.tb.pdfly.parameter.hasStoragePermission
import com.tb.pdfly.utils.applife.HotStartManager
import com.tb.pdfly.utils.isFirstAskStorage

abstract class BaseFilePermissionActivity<T : ViewBinding>(inflate: (layoutInflater: LayoutInflater) -> T) : BaseActivity<T>(inflate) {

    open fun onStoragePermissionGranted() = Unit

    private val permissionArr by lazy { arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) }

    private val permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (hasStoragePermission()) {
            onStoragePermissionGranted()
        }
    }

    private val setResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        HotStartManager.navigateToSettingPage(false)
        if (hasStoragePermission()) {
            onStoragePermissionGranted()
        }
    }

    fun checkStoragePermission() {
        if (hasStoragePermission()) {
            onStoragePermissionGranted()
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            if (isFirstAskStorage) {
                isFirstAskStorage = false
                permissionResultLauncher.launch(permissionArr)
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                permissionResultLauncher.launch(permissionArr)
            } else {
                runCatching {
                    HotStartManager.navigateToSettingPage(true)
                    setResultLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = "package:${packageName}".toUri()
                        addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    })
                }.onFailure {
                    Toast.makeText(this, getString(R.string.an_unknown_error_occurred), Toast.LENGTH_LONG).show()
                }
            }
        } else {
            setResultLauncher.launch(Intent(this, FilePermissionAskActivity::class.java))
        }
    }


}