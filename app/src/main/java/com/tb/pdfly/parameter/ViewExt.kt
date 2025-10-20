package com.tb.pdfly.parameter

import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import com.tb.pdfly.R

fun View.startRotateAnim(duration: Long = 500L, repeatCount: Int = ObjectAnimator.INFINITE) {

    if (this.getTag(R.id.rotation_animator_tag) != null) return
    val animator = ObjectAnimator.ofFloat(this, View.ROTATION, 0f, 360f).apply {
        this.duration = duration
        this.repeatCount = repeatCount
        this.interpolator = LinearInterpolator()
        this.repeatMode = ObjectAnimator.RESTART
        this.start()
    }
    this.setTag(R.id.rotation_animator_tag, animator)
}

fun View.stopRotateAnim() {
    val animator = this.getTag(R.id.rotation_animator_tag) as? ObjectAnimator
    animator?.let {
        it.cancel()
        this.setTag(R.id.rotation_animator_tag, null)
    }
    this.rotation = 0f
}