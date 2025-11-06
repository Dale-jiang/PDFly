package com.tb.pdfly.page.web

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.databinding.ActivityWebViewBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.WEB_URL_KEY
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WebViewActivity : BaseActivity<ActivityWebViewBinding>(ActivityWebViewBinding::inflate) {

    private val blankUrl = "about:blank"
    private val _url by lazy { intent?.getStringExtra(WEB_URL_KEY) ?: blankUrl }

    override fun initView() {
        binding.title.text = getString(R.string.app_name)
        onBackPressedDispatcher.addCallback {
            showBackAd {
                finish()
            }
        }
        binding.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        with(binding.webView) {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            webChromeClient = createWebChromeClient()
            loadUrl(_url)
        }
    }

    private fun createWebChromeClient() = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView?, title: String?) {
            binding.title.text = title
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            binding.progressBar.apply {
                progress = newProgress
                isVisible = newProgress < 100
            }
        }
    }

    private fun showBackAd(callBack: CallBack) {
        if (AdCenter.adNoNeededShow()) {
            callBack()
            return
        }
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "pdfly_back_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyBackInt
            if (ad.canShow(this@WebViewActivity)) {
                ad.showFullAd(this@WebViewActivity, "pdfly_back_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@WebViewActivity)
                callBack()
            }
        }
    }

    override fun onDestroy() {
        with(binding.webView) {
            runCatching {
                clearHistory()
                clearCache(true)
                loadUrl(blankUrl)
                removeAllViews()
                destroy()
            }
        }
        super.onDestroy()
    }

}