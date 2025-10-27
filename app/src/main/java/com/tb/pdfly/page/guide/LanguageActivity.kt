package com.tb.pdfly.page.guide

import android.annotation.SuppressLint
import android.content.Intent
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityLanguageBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.adapter.LanguageAdapter
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.app
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.parameter.updateResources
import com.tb.pdfly.utils.defaultLocalCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LanguageActivity : BaseActivity<ActivityLanguageBinding>(ActivityLanguageBinding::inflate) {

    companion object {
        const val INTENT_KEY = "isFromSetting"
    }

    private val languages by lazy {
        listOf(
            "English" to "en",
            "繁體中文" to "zh",
            "日本語" to "ja",
            "한국어" to "ko",
            "Italiano" to "it",
            "Deutsch" to "de",
            "Français" to "fr",
            "Português" to "pt",
            "Español" to "es",
            "ภาษาไทย" to "th",
            "Bahasa Indonesia" to "in",
            "हिन्दी" to "hi",
            "العربية" to "ar"
        )
    }

    private val isFromSetting by lazy { intent?.getBooleanExtra(INTENT_KEY, true) ?: true }
    private lateinit var mAdapter: LanguageAdapter
    private var autoNextJob: Job? = null

    override fun initView() {
        binding.apply {

            ivBack.isVisible = isFromSetting
            ivBack.setOnClickListener { if (isFromSetting) finish() }
            btnNext.setOnClickListener {
                autoNextJob?.cancel()
                toNextView()
            }


            lifecycleScope.launch(Dispatchers.Main) {
                val listData = languages.toMutableList().sortedByDescending { it.second == defaultLocalCode }
                var defaultIndex = listData.indexOfFirst { defaultLocalCode == it.second }
                if (defaultIndex == -1) defaultIndex = 0
                mAdapter = LanguageAdapter(this@LanguageActivity, defaultIndex, listData)
                binding.recyclerView.itemAnimator = null
                binding.recyclerView.adapter = mAdapter
                startAutoNext()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startAutoNext() {
        if (!isFromSetting) {
            autoNextJob = lifecycleScope.launch(Dispatchers.Main) {
                binding.btnNext.text = "${getString(R.string.next)}(3)"
                delay(1000)
                binding.btnNext.text = "${getString(R.string.next)}(2)"
                delay(1000)
                binding.btnNext.text = "${getString(R.string.next)}(1)"
                delay(1000)
                toNextView()
            }
        }
    }

    private fun toNextView() {
        defaultLocalCode = mAdapter.data[mAdapter.currentIndex].second
        app.updateResources()
        if (isFromSetting) {
            toActivity<MainActivity>(finishCurrent = true) { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
        } else {
            // TODO:
            toActivity<MainActivity>(finishCurrent = true) { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
        }
    }


}