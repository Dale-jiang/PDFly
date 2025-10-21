package com.tb.pdfly.page.base

import android.annotation.SuppressLint
import android.content.Intent
import android.provider.Settings
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.databinding.ActivityPermissionAskBinding
import com.tb.pdfly.parameter.hasStoragePermission
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.utils.applife.HotStartManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FilePermissionAskActivity : BaseActivity<ActivityPermissionAskBinding>(ActivityPermissionAskBinding::inflate) {

    private var checkTicker: Job? = null
    private var haveGoSet: Boolean = false

    override fun onResume() {
        super.onResume()
        if (haveGoSet) {
            finish()
            return
        }

        haveGoSet = true
        lifecycleScope.launch {
            HotStartManager.navigateToSettingPage(true)
            gpSpecialSettings()
        }
        startChecker()
    }

    private fun startChecker() {
        checkTicker?.cancel()
        checkTicker = lifecycleScope.launch(Dispatchers.IO) {

            runCatching {
                while (!hasStoragePermission()) {
                    delay(200)
                }
                if (!isFinishing) {
                    checkTicker?.cancel()
                    toActivity<FilePermissionAskActivity>(finishCurrent = true) {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun gpSpecialSettings() {
        runCatching {
            startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).also {
                it.data = "package:${packageName}".toUri()
                it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            })
        }.onFailure {
            runCatching {
                startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).also {
                    it.data = "package:${packageName}".toUri()
                    it.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                })
            }.onFailure {
                finish()
            }
        }
    }

    override fun onDestroy() {
        HotStartManager.navigateToSettingPage(false)
        super.onDestroy()
        checkTicker?.cancel()
        checkTicker = null
    }


}