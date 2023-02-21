package com.android.atlasv.topon_demo

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.android.atlasv.ad.framework.ad.BaseAd
import com.android.atlasv.ad.framework.core.AdManager
import com.android.atlasv.ad.framework.core.AdType
import com.android.atlasv.ad.framework.event.AnalyticsListener
import com.android.atlasv.ad.framework.util.AdLog

/**
 * Created by woyanan on 2020/8/12
 */
object AdHelper {
    private val list = arrayListOf(
        AdType.INTERSTITIAL to Constants.INTERSTITIAL_AD_ID,
        AdType.BANNER to Constants.BANNER_AD_ID,
        AdType.NATIVE to Constants.NATIVE_AD_ID
    )
    private var dict: HashMap<String, BaseAd> = HashMap()

    fun init(context: Context) {
        if (BuildConfig.DEBUG) {
            AdLog.setLogLevel(Log.VERBOSE)
        }
        AdManager.setAnalyticsListener(object : AnalyticsListener {
            override fun logEvent(event: String, bundle: Bundle?) {

            }
        })
    }

    fun build(context: Context) {
        dict.clear()
        list.forEach {
            AdManager.buildAd(context, it.first, it.second, Constants.AD_PLATFORM)?.apply {
                dict[it.second] = this
            }
        }
    }

    fun getAd(adId: String): BaseAd? {
        AdLog.w("AdHelper") { "getAd $adId" }
        return dict[adId]
    }
}