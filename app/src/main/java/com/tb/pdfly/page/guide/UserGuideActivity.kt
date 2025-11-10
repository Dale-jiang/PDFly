package com.tb.pdfly.page.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tb.pdfly.R
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.interfaces.IAd
import com.tb.pdfly.databinding.ActivityUserGuideBinding
import com.tb.pdfly.databinding.ItemImageBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.CallBack
import com.tb.pdfly.parameter.dpToPx
import com.tb.pdfly.parameter.toActivity
import com.tb.pdfly.report.ReportCenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserGuideActivity : BaseActivity<ActivityUserGuideBinding>(ActivityUserGuideBinding::inflate) {

    private var currentIndex = 0
    private val contents by lazy {
        listOf(R.string.guide_des_1, R.string.guide_des_2)
    }
    private val images by lazy {
        listOf(R.drawable.image_guide_1, R.drawable.image_guide_2)
    }

    override fun initView() {

        ReportCenter.reportManager.report("guide_feature_show_count")

        onBackPressedDispatcher.addCallback {}

        binding.btnNext.setOnClickListener {
            ReportCenter.reportManager.report("guide_feature_confirm_click_count")
            if (currentIndex < 1) {
                currentIndex++
                binding.viewPager.currentItem = currentIndex
            } else {
                showNextAd {
                    toActivity<MainActivity>(finishCurrent = true)
                }
            }
        }
        binding.btnSkip.setOnClickListener {
            ReportCenter.reportManager.report("guide_feature_skip_click_count")
            showNextAd {
                toActivity<MainActivity>(finishCurrent = true)
            }
        }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentIndex = position
                binding.btnNext.text = if (position >= 1) getString(R.string.start) else getString(R.string.next)
                changeIndicatorWidth(position)
                if (position != 0) showNatAd()

            }
        })
        binding.viewPager.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
                fun bind(imageRes: Int) {
                    binding.imageView.setImageResource(imageRes)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ImageViewHolder(binding)
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as ImageViewHolder).bind(images[position])
            }

            override fun getItemCount(): Int = images.size
        }
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        showNatAd()
    }

    private fun changeIndicatorWidth(position: Int) {
        when (position) {
            1 -> {
                binding.textDes.text = getString(contents[1])
                binding.indicator1.updateLayoutParams { width = dpToPx(5f).toInt() }
                binding.indicator2.updateLayoutParams { width = dpToPx(16f).toInt() }
            }

            else -> {
                binding.textDes.text = getString(contents[0])
                binding.indicator1.updateLayoutParams { width = dpToPx(16f).toInt() }
                binding.indicator2.updateLayoutParams { width = dpToPx(5f).toInt() }
            }
        }
    }

    private fun showNextAd(callBack: CallBack) {
        ReportCenter.reportManager.report("pdfly_ad_chance", mapOf("ad_pos_id" to "new_intro_int"))
        lifecycleScope.launch {
            while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
            val ad = AdCenter.pdflyBackInt
            if (ad.canShow(this@UserGuideActivity)) {
                ad.showFullAd(this@UserGuideActivity, "new_intro_int", showLoading = true) { callBack() }
            } else {
                ad.loadAd(this@UserGuideActivity)
                callBack()
            }
        }
    }


    private var ad: IAd? = null
    private fun showNatAd() {
        if (AdCenter.adNoNeededShow()) return
        ReportCenter.reportManager.report("pdfly_ad_chance", hashMapOf("ad_pos_id" to "new_intro_nat"))
        val nAd = AdCenter.pdflyScanNat
        nAd.waitingNativeAd(this@UserGuideActivity) {
            lifecycleScope.launch {
                while (!lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) delay(200L)
                if (nAd.canShow(this@UserGuideActivity)) {
                    binding.adContainer.apply {
                        ad?.destroy()
                        nAd.showNativeAd(this@UserGuideActivity, this, "new_intro_nat") {
                            ad = it
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ad?.destroy()
    }

}