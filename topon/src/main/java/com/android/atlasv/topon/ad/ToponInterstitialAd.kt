package com.android.atlasv.topon.ad

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.android.atlasv.ad.framework.ad.BaseAd
import com.android.atlasv.ad.framework.core.AdType
import com.android.atlasv.ad.framework.event.AdValueWrapper
import com.android.atlasv.ad.framework.event.AnalysisStatus
import com.android.atlasv.ad.framework.event.EventAgent
import com.android.atlasv.ad.framework.util.AdLog
import com.android.atlasv.topon.ToponAdFactory
import com.android.atlasv.topon.getNetworkName
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoEventListener


class ToponInterstitialAd(context: Context, private val adUnitId: String) : BaseAd() {
    companion object {
        const val TAG = "ToponInterstitialAd"
    }

    private val bundle = Bundle()
    override var placement: String? = null
        set(value) {
            field = value
            bundle.putString("placement", value)
        }

    init {
        bundle.putString("unit_id", adUnitId)
    }

    override fun prepare() {
        super.prepare()
        AdLog.w(TAG) { "prepare $placement $adUnitId" }
        ATInterstitialAutoAd.addPlacementId(adUnitId)
    }

    override fun show(activity: Activity): Boolean {
        if (!ATInterstitialAutoAd.isAdReady(adUnitId)) {
            AdLog.w(TAG) { "Interstitial Ad did not load $placement $adUnitId" }
            EventAgent.logAdShow(
                activity.applicationContext,
                adUnitId,
                false,
                AnalysisStatus.LOAD_NOT_COMPLETED.getValue()
            )
            return false
        }
        ATInterstitialAutoAd.show(activity, adUnitId, object : ATInterstitialAutoEventListener() {
            override fun onInterstitialAdClicked(info: ATAdInfo) {
                AdLog.w(TAG) { "onAdClicked $placement $adUnitId" }

                adListener?.onAdClicked()
            }

            override fun onInterstitialAdShow(info: ATAdInfo) {
                AdLog.w(TAG) { "onAdOpened $placement $adUnitId" }
                EventAgent.logAdValue(
                    AdValueWrapper(
                        ToponAdFactory.PLATFORM,
                        info.publisherRevenue.toFloat(),
                        info.currency,
                        info.ecpmPrecision,
                        info.networkFirmId.getNetworkName(),
                        info.topOnPlacementId
                    )
                )

                adListener?.onAdImpression()
                adListener?.onAdOpened()
            }

            override fun onInterstitialAdClose(info: ATAdInfo) {
                AdLog.w(TAG) { "onAdClosed $placement $adUnitId" }

                adListener?.onAdClosed()
            }

            override fun onInterstitialAdVideoStart(info: ATAdInfo) {
            }

            override fun onInterstitialAdVideoEnd(info: ATAdInfo) {
            }

            override fun onInterstitialAdVideoError(error: AdError) {
            }

        })
        EventAgent.logAdShow(
            activity.applicationContext,
            adUnitId,
            true,
            AnalysisStatus.SUCCESS.getValue()
        )
        return true
    }

    override fun getAdPlatform(): String {
        return ToponAdFactory.PLATFORM
    }

    override fun getAdType(): Int {
        return AdType.INTERSTITIAL
    }

    override fun isReady(): Boolean {
        return ATInterstitialAutoAd.isAdReady(adUnitId)
    }
}