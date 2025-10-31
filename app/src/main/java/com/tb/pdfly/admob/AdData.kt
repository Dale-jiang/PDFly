package com.tb.pdfly.admob

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
@Keep
data class AdConfig(
    @SerializedName("pdfly_launch") val pdflyLaunch: List<AdConfigItem> = emptyList(),
    @SerializedName("pdfly_scan_int") val pdflyScanInt: List<AdConfigItem> = emptyList(),
    @SerializedName("pdfly_back_int") val pdflyBackInt: List<AdConfigItem> = emptyList(),
    @SerializedName("pdfly_main_nat") val pdflyMainNat: List<AdConfigItem> = emptyList(),
    @SerializedName("pdfly_scan_nat") val pdflyScanNat: List<AdConfigItem> = emptyList(),
    @SerializedName("pdfly_main_ban") val pdflyMainBan: List<AdConfigItem> = emptyList()
) : Parcelable

@Keep
@Parcelize
data class AdConfigItem(
    @SerializedName("pdfly_id") val adId: String,
    @SerializedName("pdfly_amtt") val adPlatform: String,
    @SerializedName("pdfly_tyr") val adType: String,
    @SerializedName("pdfly_time") val adExpireTime: Int,
    @SerializedName("pdfly_top") val adWeight: Int
) : Parcelable

const val AD_JSON = """
    {
  "pdfly_launch": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/9257395921",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "op",
      "pdfly_time": 13800,
      "pdfly_top": 3
    }
  ],
    "pdfly_scan_int": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/1033173712",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "int",
      "pdfly_time": 3000,
      "pdfly_top": 3
    }
  ],
  "pdfly_back_int": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/1033173712",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "int",
      "pdfly_time": 3000,
      "pdfly_top": 3
    }
  ],
  "pdfly_main_nat": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/2247696110",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "nat",
      "pdfly_time": 3000,
      "pdfly_top": 3
     }
  ],
  "pdfly_scan_nat": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/2247696110",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "nat",
      "pdfly_time": 3000,
      "pdfly_top": 3
           }
  ],
  "pdfly_main_ban": [
    {
      "pdfly_id": "ca-app-pub-3940256099942544/9214589741",
      "pdfly_amtt": "admob",
      "pdfly_tyr": "ban",
      "pdfly_time": 3000,
      "pdfly_top": 3
    }
  ]
}
"""