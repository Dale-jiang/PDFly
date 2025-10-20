package com.tb.pdfly.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CountdownTimer(
    private val totalTimeMillis: Long,
    private val intervalMillis: Long = 1000L,
    private val onTick: (Long) -> Unit,
    private val onFinish: () -> Unit
) {

    private var remainingTime: Long = totalTimeMillis
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())


    fun start() {

        if (job?.isActive == true) {
            return
        }

        job = coroutineScope.launch {
            val startTime = System.currentTimeMillis()
            while (remainingTime > 0) {
                onTick(remainingTime)
                delay(intervalMillis)
                val elapsedTime = System.currentTimeMillis() - startTime
                remainingTime = totalTimeMillis - elapsedTime
                if (remainingTime < 0) {
                    remainingTime = 0
                }
            }
            onFinish()
        }
    }


    fun stop() {
        job?.cancel()
        job = null
    }


    fun reset() {
        stop()
        remainingTime = totalTimeMillis
    }


    fun cancel() {
        coroutineScope.cancel()
        job = null
    }

}