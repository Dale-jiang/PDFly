package com.tb.pdfly.parameter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.TypedValue
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun AppCompatActivity.myEnableEdgeToEdge(parentView: ViewGroup? = null, topPadding: Boolean = true, bottomPadding: Boolean = true) {
    runCatching {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            if (parentView != null) {
                parentView.setPadding(0, if (topPadding) systemBarsInsets.top else 0, 0, if (bottomPadding) systemBarsInsets.bottom else 0)
            } else {
                this@myEnableEdgeToEdge.window.decorView.setPadding(0, if (topPadding) systemBarsInsets.top else 0, 0, if (bottomPadding) systemBarsInsets.bottom else 0)
            }
            insets
        }
    }.onFailure { throwable ->
        throwable.printStackTrace()
    }
}

@Suppress("DEPRECATION")
fun AppCompatActivity.setDensity() {
    resources.displayMetrics.apply {
        density = heightPixels / 765f
        densityDpi = (density * 160).toInt()
        scaledDensity = density
    }
}

inline fun <reified T : AppCompatActivity> Activity.toActivity(finishCurrent: Boolean = false, block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java).apply(block)
    startActivity(intent)
    if (finishCurrent) finish()
}

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
}
