package com.android.atlasv.topon

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.android.atlasv.ad.framework.ad.BaseAd
import com.android.atlasv.ad.framework.core.AdType
import com.android.atlasv.ad.framework.core.IAdFactory
import com.android.atlasv.ad.framework.event.AnalysisEvent
import com.android.atlasv.ad.framework.event.EventAgent
import com.android.atlasv.ad.framework.util.AdLog
import com.android.atlasv.topon.ad.ToponBannerAd
import com.android.atlasv.topon.ad.ToponInterstitialAd
import com.android.atlasv.topon.ad.ToponNativeAd
import com.anythink.core.api.ATSDK
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener

object ToponAdFactory : IAdFactory() {
    const val PLATFORM = "topon"

    override fun buildAd(
        context: Context,
        type: Int,
        adId: String,
        loadLayoutId: Int,
        inlineBanner: Boolean
    ): BaseAd? {
        return when (type) {
            AdType.BANNER -> ToponBannerAd(context as Activity, adId)
            AdType.INTERSTITIAL -> ToponInterstitialAd(context, adId)
            AdType.NATIVE -> ToponNativeAd(context as Activity, adId)
            else -> null
        }
    }

    override fun getAdPlatform(): String {
        return PLATFORM
    }

    override fun initializePlatformSdk(context: Context) {
        ATSDK.setNetworkLogDebug(AdLog.adLogEnable(Log.ERROR))
        ATSDK.integrationChecking(context)
        val metaData = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        ).metaData
        val appId = metaData.getString("com.topon.APP_ID")
        val appKey = metaData.getString("com.topon.APP_KEY")
        ATSDK.init(context, appId, appKey)
        ATInterstitialAutoAd.init(context, emptyArray(), object : ATInterstitialAutoLoadListener {
            override fun onInterstitialAutoLoaded(placement: String) {
                val bundle = Bundle()
                bundle.putString("unit_id", placement)
                EventAgent.logEvent(
                    context,
                    AnalysisEvent.AD_LOAD_SUCCESS, bundle
                )
            }

            override fun onInterstitialAutoLoadFail(placement: String, error: AdError) {
                val bundle = Bundle()
                bundle.putString("unit_id", placement)
                bundle.putInt("errorCode", error.code.toInt())
                EventAgent.logEvent(context, AnalysisEvent.AD_LOAD_FAIL, bundle)
            }

        })
    }

    override fun setActivityClasses4LoadAds(activitySet: Set<Class<out Activity>>) {
    }

    override fun setTestDeviceIds(testDeviceIds: List<String>) {
    }
}