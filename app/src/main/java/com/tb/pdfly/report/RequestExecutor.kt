package com.tb.pdfly.report

import com.tb.pdfly.parameter.showLog
import com.tb.pdfly.report.ReportCenter.httpClient
import com.tb.pdfly.report.ReportCenter.tbaUrl
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

open class RequestExecutor {

    protected suspend fun runRequest(requestTag: String, bodyString: String) {
        "[$requestTag] Start request with body:\n$bodyString".showLog("RequestExecutor")
        val requestBody = bodyString.toRequestBody("application/json".toMediaTypeOrNull())
        val request = createRequest(requestBody)
        executeWithRetry(request, requestTag)
    }

    private fun createRequest(body: RequestBody): Request {
        return Request.Builder().url(tbaUrl).post(body).build()
    }

    private suspend fun executeWithRetry(request: Request, tag: String) {
        var attempt = 0

        while (attempt < 5) {
            try {
                val response = runRequestAsync(request)

                if (response.isSuccessful) {
                    val result = response.body?.string().orEmpty()
                    "[$tag] Success (attempt=$attempt)\nResult: $result".showLog("RequestExecutor")
                    return
                } else {
                    "[$tag] Non-200 response (attempt=$attempt): ${response.code}".showLog("RequestExecutor")
                }

            } catch (e: IOException) {
                "[$tag] Network error (attempt=$attempt): ${e.message}".showLog("RequestExecutor")
            }

            attempt++
            if (attempt < 5) {
                "[$tag] Retry attempt $attempt after 60_000 ms ...".showLog("RequestExecutor")
                delay(60_1000L)
            }
        }
        "[$tag] All attempts failed. Giving up.".showLog("RequestExecutor")
    }

    private suspend fun runRequestAsync(request: Request): Response =
        suspendCancellableCoroutine { continuation ->
            httpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) continuation.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuation.isActive) continuation.resume(response)
                }
            })
        }

}