package com.tb.pdfly.page.guide

import androidx.activity.addCallback
import com.tb.pdfly.databinding.ActivityFirstLoadingBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.myEnableEdgeToEdge
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.utils.CountdownTimer
import com.tb.pdfly.utils.isFirstLaunch

class FirstLoadingActivity : BaseActivity<ActivityFirstLoadingBinding>(ActivityFirstLoadingBinding::inflate) {

    private val keyUninstall by lazy { intent?.getStringExtra("key_uninstall") }

    private val countdownTimer by lazy {
        CountdownTimer(
            totalTimeMillis = 10_000L,
            intervalMillis = 1000L,
            onTick = { millisUntilFinished ->
                val secondsRemaining = millisUntilFinished / 1000
                if (secondsRemaining <= 7) {
                    doNext()
                }
            },
            onFinish = {
                doNext()
            }
        )
    }

    override fun onAttachedToWindow() {
        myEnableEdgeToEdge(binding.container, topPadding = true, bottomPadding = true)
    }

    override fun initView() {
        onBackPressedDispatcher.addCallback { }
        countdownTimer.start()
    }


    private fun doNext() {
        countdownTimer.stop()

        when (keyUninstall) {
            "shortcut_view" -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }

            "shortcut_scan" -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }

            "shortcut_uninstall" -> {
                toActivity<UninstallStep1Activity>(finishCurrent = true)
            }

            else -> {
                toGuideIfNeeded {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }
        }


    }

    private fun toGuideIfNeeded(next: () -> Unit) {
        if (isFirstLaunch) {
            isFirstLaunch = false
            toActivity<LanguageActivity>(finishCurrent = true) {
                putExtra(LanguageActivity.INTENT_KEY, false)
            }
            return
        }
        next.invoke()
    }


    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

}