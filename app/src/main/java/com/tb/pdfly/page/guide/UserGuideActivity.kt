package com.tb.pdfly.page.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tb.pdfly.R
import com.tb.pdfly.databinding.ActivityUserGuideBinding
import com.tb.pdfly.databinding.ItemImageBinding
import com.tb.pdfly.page.MainActivity
import com.tb.pdfly.page.base.BaseActivity
import com.tb.pdfly.parameter.dpToPx
import com.tb.pdfly.parameter.toActivity

class UserGuideActivity : BaseActivity<ActivityUserGuideBinding>(ActivityUserGuideBinding::inflate) {

    private var currentIndex = 0
    private val contents by lazy {
        listOf(R.string.guide_des_1, R.string.guide_des_2)
    }
    private val images by lazy {
        listOf(R.drawable.image_guide_1, R.drawable.image_guide_2)
    }

    override fun initView() {
        onBackPressedDispatcher.addCallback {}

        binding.btnNext.setOnClickListener {
            if (currentIndex < 1) {
                currentIndex++
                binding.viewPager.currentItem = currentIndex
            } else {
                toActivity<MainActivity>(finishCurrent = true)
            }
        }
        binding.btnSkip.setOnClickListener {
            toActivity<MainActivity>(finishCurrent = true)
        }


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentIndex = position
                binding.btnNext.text = if (position >= 1) getString(R.string.start) else getString(R.string.next)
                changeIndicatorWidth(position)
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

}