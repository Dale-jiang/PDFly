package com.tb.pdfly.page.guide

import android.content.Intent
import android.provider.Settings
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import com.tb.pdfly.databinding.ActivityUninstallStep2Binding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.page.dialog.UninstallDialog
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.utils.applife.HotStartManager

class UninstallStep2Activity : BaseActivity<ActivityUninstallStep2Binding>(ActivityUninstallStep2Binding::inflate) {

    private val detailsResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        HotStartManager.navigateToSettingPage(false)
        if (result.resultCode == RESULT_OK || result.resultCode == RESULT_CANCELED) {
            goBackHome()
        }
    }

    override fun initView() {

        onBackPressedDispatcher.addCallback { goBackHome() }

        binding.apply {
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            btnKeep.setOnClickListener { goBackHome() }
            btnUninstall.setOnClickListener {

                UninstallDialog {
                    if (it) {
                        HotStartManager.navigateToSettingPage(true)
                        detailsResultLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = "package:${packageName}".toUri()
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        })
                    } else {
                        goBackHome()
                    }

                }.show(supportFragmentManager, "uninstall")


            }
        }

    }


    private fun goBackHome() {
        toActivity<MainActivity>(finishCurrent = true) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }
}