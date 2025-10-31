package com.tb.pdfly.utils

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.admob.AD_JSON
import com.tb.pdfly.admob.AdCenter.buyUserTags
import com.tb.pdfly.admob.AdCenter.pdfly_open
import com.tb.pdfly.parameter.showLog
import org.json.JSONObject

object RemoteConfigUtils {

    private val remoteConfig by lazy {
        Firebase.remoteConfig.apply { setConfigSettingsAsync(configSettings) }
    }

    private val configSettings by lazy {
        remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
    }

    fun initRemoteConfig() {
        if (BuildConfig.DEBUG) {
//            AdCenter.initData()
            return
        }
        fetchAndActivateRemoteConfig()
    }

    private fun fetchAndActivateRemoteConfig() {
        getAllConfigs()
        remoteConfig.fetchAndActivate().addOnSuccessListener {
            getAllConfigs()
        }
    }

    private fun getAllConfigs() {
        getAdConfig()
        getReferConfig()
    }

    private fun getAdConfig() {
        val json = runCatching {
            val json = remoteConfig["pdfly_ad_config"].asString()
            "RemoteConfig AdConfig loaded: $json".showLog()
            json.ifEmpty { AD_JSON }
        }.getOrElse {
            AD_JSON
        }
//        AdCenter.initData(json)
    }

    private fun getReferConfig() {
        runCatching {
            val json = remoteConfig["pdfly_refer_user"].asString()
            if (json.isNotEmpty()) {
                "RemoteConfig ReferConfig loaded: $json".showLog()
                JSONObject(json).apply {
                    pdfly_open = optInt("pdfly_open", 1)
                    val referJsonArray = optJSONArray("pdfly_re")
                    referJsonArray?.let {
                        buyUserTags.clear()
                        for (i in 0 until referJsonArray.length()) {
                            buyUserTags.add(referJsonArray.getString(i))
                        }
                    }
                }
            }
        }
    }


}