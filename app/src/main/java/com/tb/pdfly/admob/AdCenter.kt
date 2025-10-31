package com.tb.pdfly.admob

import android.os.Build
import com.google.gson.Gson
import com.tb.pdfly.admob.loader.FullAdLoader
import com.tb.pdfly.utils.installReferrerStr

object AdCenter {

    var pdfly_open = 1
    val buyUserTags = mutableListOf(
        "fb4a", "instagram", "ig4a",
        "gclid", "not%20set", "youtubeads",
        "%7B%22", "adjust", "bytedance",
        "livead"
    )

    val countryCodeList by lazy {
        listOf(
            "AT", "BE", "BG", "HR",
            "CY", "CZ", "DK", "EE", "FI",
            "SI", "ES", "SE", "NO", "IS", "LI",
            "FR", "DE", "GR", "HU", "IE", "IT",
            "LV", "LT", "LU", "MT", "NL", "PL", "PT",
            "RO", "SK", "CH", "GB"
        )
    }

    val pdflyLaunch by lazy { FullAdLoader("pdfly_launch") }
    val pdflyScanInt by lazy { FullAdLoader("pdfly_scan_int") }
    val pdflyBackInt by lazy { FullAdLoader("pdfly_back_int") }

    fun initAdConfig(json: String = AD_JSON) {
        val parsedData = runCatching {
            Gson().fromJson(json, AdConfig::class.java)
        }.getOrElse { null }

        parsedData?.let {
            pdflyLaunch.initData(it.pdflyLaunch.toMutableList())
            pdflyScanInt.initData(it.pdflyScanInt.toMutableList())
            pdflyBackInt.initData(it.pdflyBackInt.toMutableList())
        }
    }

    fun adNoNeededShow(): Boolean {
        if (isEmulator()) return true
        if (pdfly_open == 0) return false // 0 不对任何用户屏蔽
        if (isFuckingUser()) return true
        if (pdfly_open == 1 && !isFather()) return true
        return false
    }

    fun isFuckingUser(): Boolean = run {
        // TODO:
        return false
    }

    fun isFather(): Boolean = run {
        val refer = installReferrerStr
        if (refer.isEmpty()) return true
        return buyUserTags.any { refer.contains(it, true) }
    }

    private fun isEmulator(): Boolean = run {
        if (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) {
            return true
        }
        val fingerprint = Build.FINGERPRINT
        if (fingerprint.startsWith("generic") || fingerprint.startsWith("unknown")) {
            return true
        }
        val hardware = Build.HARDWARE
        if (hardware.contains("goldfish") || hardware.contains("ranchu")) {
            return true
        }
        val model = Build.MODEL
        if (model.contains("google_sdk") ||
            model.contains("Emulator") ||
            model.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.HOST.startsWith("Build")
        ) {
            return true
        }
        val product = Build.PRODUCT
        return product.contains("sdk_google") ||
                product.contains("google_sdk") ||
                product.contains("sdk") ||
                product.contains("sdk_x86") ||
                product.contains("sdk_gphone64_arm64") ||
                product.contains("vbox86p") ||
                product.contains("emulator") ||
                product.contains("simulator")
    }
}