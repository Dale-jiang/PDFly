package com.tb.pdfly.notice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.tb.pdfly.notice.service.FrontJobIntentService
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter
import com.tb.pdfly.utils.CommonUtils
import com.tb.pdfly.utils.firstCountryCode
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MessageTaskWorker {

    private val taskScope by lazy { CoroutineScope(Dispatchers.IO + SupervisorJob() + CoroutineExceptionHandler { _, error -> "Error: ${error.message}".showLog() }) }

    private val unlockReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                taskScope.launch {
                    delay(800L)
                    withContext(Dispatchers.Main) {
                        MessageManager.showNotice("unlock")
                    }
                    context?.let {
                        runCatching {
                            FrontJobIntentService.start(context)
                        }
                    }
                }
            }
        }
    }

    fun startMessageTask() = runCatching {

        startBackSession()

        if ("KR" == firstCountryCode && CommonUtils.isSamsungDevice()) return@runCatching
        startTimer()
        app.registerReceiver(unlockReceiver, IntentFilter().also {
            it.addAction(Intent.ACTION_USER_PRESENT)
        })
    }

    private fun tickerFlow(first: Long, interval: Long) = flow {
        delay(first)
        while (true) {
            emit(Unit)
            delay(interval)
        }
    }

    private fun startTimer() {
        taskScope.launch {
            tickerFlow(1_000L, 60000L).collect {
                withContext(Dispatchers.Main) {
                    MessageManager.showNotice("time")
                }
            }
        }
    }

    private fun startBackSession() {
        taskScope.launch {
            tickerFlow(1000L, 15 * 60000L).collect {
                ReportCenter.reportManager.report("sess_back")
            }
        }
    }

}