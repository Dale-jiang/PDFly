package com.tb.pdfly.page

import androidx.activity.addCallback
import com.tb.pdfly.databinding.ActivityFirstLoadingBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.myEnableEdgeToEdge
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.utils.CountdownTimer

class FirstLoadingActivity : BaseActivity<ActivityFirstLoadingBinding>(ActivityFirstLoadingBinding::inflate) {


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
        toActivity<MainActivity>(finishCurrent = true)
    }


    override fun onDestroy() {
        super.onDestroy()
        countdownTimer.cancel()
    }

}

