package com.tb.pdfly.utils

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.google.gson.Gson
import com.tb.pdfly.BuildConfig
import com.tb.pdfly.admob.AD_JSON
import com.tb.pdfly.admob.AdCenter
import com.tb.pdfly.admob.AdCenter.buyUserTags
import com.tb.pdfly.admob.AdCenter.pdfly_open
import com.tb.pdfly.admob.AdmobRevenueManager.adltvTop10
import com.tb.pdfly.admob.AdmobRevenueManager.adltvTop20
import com.tb.pdfly.admob.AdmobRevenueManager.adltvTop30
import com.tb.pdfly.admob.AdmobRevenueManager.adltvTop40
import com.tb.pdfly.admob.AdmobRevenueManager.adltvTop50
import com.tb.pdfly.notice.JumpType
import com.tb.pdfly.notice.MessageManager
import com.tb.pdfly.notice.NoticeConfig
import com.tb.pdfly.notice.NoticeContent
import com.tb.pdfly.notice.NoticeType
import com.tb.pdfly.notice.testJson
import com.tb.pdfly.parameter.showLog
import org.json.JSONArray
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
            AdCenter.initAdConfig()
            MessageManager.remoteNoticeConfig = Gson().fromJson(testJson, NoticeConfig::class.java)
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
        getNoticeConfig()
        getNoticeContent()
        getTopPercentConfig()
    }


    private fun getTopPercentConfig() {
        runCatching {
            val jsonString = remoteConfig["pdfly_daytop_percent"].asString()
            if (jsonString.isBlank()) {
                return
            }
            val jsonObject = JSONObject(jsonString)
            adltvTop10 = jsonObject.optDouble("pdfly_oneday_top10", 1.0)
            adltvTop20 = jsonObject.optDouble("pdfly_oneday_top20", 0.8)
            adltvTop30 = jsonObject.optDouble("pdfly_oneday_top30", 0.6)
            adltvTop40 = jsonObject.optDouble("pdfly_oneday_top40", 0.5)
            adltvTop50 = jsonObject.optDouble("pdfly_oneday_top50", 0.1)

        }.onFailure { exception ->
            "RemoteConfig Failed to get top percent: ${exception.message}".showLog()
        }
    }

    private fun getAdConfig() {
        val json = runCatching {
            val json = remoteConfig["pdfly_ad_config"].asString()
            "RemoteConfig AdConfig loaded: $json".showLog()
            json.ifEmpty { AD_JSON }
        }.getOrElse {
            AD_JSON
        }
        AdCenter.initAdConfig(json)
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

    private fun getNoticeConfig() {
        runCatching {
            val json = remoteConfig[if (CommonUtils.isSamsungDevice()) "pdfly_pop_sm" else "pdfly_pop"].asString()
            "RemoteCenter NoticeConfig loaded: $json".showLog()
            if (json.isNotEmpty()) {
                MessageManager.remoteNoticeConfig = Gson().fromJson(json, NoticeConfig::class.java)
            } else {
                MessageManager.remoteNoticeConfig = Gson().fromJson(testJson, NoticeConfig::class.java)
            }
        }
    }

    private fun getNoticeContent() = runCatching {
        val json = remoteConfig["pdfly_notify_text"].asString()
        if (json.isBlank()) return@runCatching
        MessageManager.remoteMessageList = parseJson(json)
    }

    private fun parseJson(jsonString: String): List<List<NoticeContent>> {
        return runCatching {
            val rootArray = JSONArray(jsonString)
            val result = mutableListOf<MutableList<NoticeContent>>()
            for (i in 0 until rootArray.length()) {
                val pageObject = rootArray.getJSONObject(i)
                val page = pageObject.getString("jump_page")
                val contentArray = pageObject.getJSONArray("noti_text")
                val jumpPair = when (page) {
                    "view" -> JumpType.HOME to 11012
                    "history" -> JumpType.HISTORY to 11013
                    "scan" -> JumpType.CREATE to 11014
                    "picture" -> JumpType.CREATE to 11015
                    "other" -> JumpType.HOME to 11016
                    else -> JumpType.HOME to 11012
                }
                val itemResult = mutableListOf<NoticeContent>()
                for (j in 0 until contentArray.length()) {
                    val contentItem = contentArray.getJSONObject(j)
                    val text = contentItem.getString("text")
                    val button = contentItem.getString("button")
                    itemResult.add(
                        NoticeContent(
                            notificationId = jumpPair.second,
                            message = text,
                            btnStr = button,
                            jumpType = jumpPair.first,
                            noticeType = NoticeType.MESSAGE,
                            isRemote = true
                        )
                    )
                }
                result.add(itemResult)
            }
            return@runCatching result
        }.getOrNull() ?: mutableListOf()
    }

}