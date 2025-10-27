package com.tb.pdfly.page.guide

import android.content.Intent
import androidx.activity.addCallback
import com.tb.pdfly.databinding.ActivityUninstallStep1Binding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.toActivity

class UninstallStep1Activity : BaseActivity<ActivityUninstallStep1Binding>(ActivityUninstallStep1Binding::inflate) {


    override fun initView() {

        onBackPressedDispatcher.addCallback {
            goBackHome()
        }

        binding.apply {
            ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
            layoutRetry.setOnClickListener { goBackHome() }
            layoutDiscover.setOnClickListener { goBackHome() }
            btnKeep.setOnClickListener { goBackHome() }
            btnUninstall.setOnClickListener {
                toActivity<UninstallStep2Activity>(finishCurrent = true)
            }
        }

    }


    private fun goBackHome() {
        toActivity<MainActivity>(finishCurrent = true) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

    }

}