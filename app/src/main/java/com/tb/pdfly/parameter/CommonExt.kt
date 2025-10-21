package com.tb.pdfly.parameter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.util.TypedValue
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

fun Context.hasStoragePermission(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager()
    else {
        mutableListOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE).all {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, it)
        }
    }
}