package com.tb.pdfly.page.web

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityWebViewBinding
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.WEB_URL_KEY

class WebViewActivity : BaseActivity<ActivityWebViewBinding>(ActivityWebViewBinding::inflate) {

    private val blankUrl = "about:blank"
    private val _url by lazy { intent?.getStringExtra(WEB_URL_KEY) ?: blankUrl }

    override fun initView() {
        binding.title.text = getString(R.string.app_name)
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