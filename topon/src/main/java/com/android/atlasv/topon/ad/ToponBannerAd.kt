package com.android.atlasv.topon.ad

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import com.android.atlasv.ad.framework.ad.BaseAd
import com.android.atlasv.ad.framework.core.AdType
import com.android.atlasv.ad.framework.event.AnalysisEvent
import com.android.atlasv.ad.framework.event.EventAgent
import com.android.atlasv.ad.framework.util.AdLog
import com.android.atlasv.topon.ToponAdFactory
import com.anythink.banner.api.ATBannerListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError

class ToponBannerAd(val activity: Activity, private val adUnitId: String) : BaseAd() {
    companion object {
        private const val TAG = "ToponBannerAd"
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

    private var bannerView: ATBannerView? = null

    override fun prepare() {
        super.prepare()
        AdLog.w(TAG) { "prepare $placement $adUnitId" }
        if (bannerView == null) {
            bannerView = ATBannerView(activity).apply {
                setPlacementId(adUnitId)
                setBannerAdListener(object : ATBannerListener {
                    override fun onBannerLoaded() {
                        AdLog.w(TAG) { "onBannerLoaded $placement $adUnitId" }
                        EventAgent.logEvent(
                            activity,
                            AnalysisEvent.AD_LOAD_SUCCESS, bundle
                        )
                        adListener?.onAdLoaded(this@ToponBannerAd)
                    }

                    override fun onBannerFailed(error: AdError) {
                        AdLog.w(TAG) { "onAdFailedToLoad.errorCode: ${error.code} $placement $adUnitId" }
                        val bundle = Bundle()
                        bundle.putString("unit_id", adUnitId)
                        bundle.putInt("errorCode", error.code.toInt())
                        EventAgent.logEvent(activity, AnalysisEvent.AD_LOAD_FAIL, bundle)

                        adListener?.onAdFailedToLoad(error.code.toInt())
                    }

                    override fun onBannerClicked(info: ATAdInfo) {
                        AdLog.w(TAG) { "onBannerClicked $placement $adUnitId" }
                        EventAgent.logEvent(activity, AnalysisEvent.AD_CLICK, bundle)

                        adListener?.onAdClicked()
                    }

                    override fun onBannerShow(info: ATAdInfo) {
                        AdLog.w(TAG) { "onBannerShow $placement $adUnitId" }
                        EventAgent.logEvent(activity, AnalysisEvent.AD_IMPRESSION, bundle)

                        adListener?.onAdImpression()
                        adListener?.onAdOpened()
                    }

                    override fun onBannerClose(info: ATAdInfo) {
                        AdLog.w(TAG) { "onAdClosed $placement $adUnitId" }
                        EventAgent.logEvent(activity, AnalysisEvent.AD_CLOSE, bundle)

                        adListener?.onAdClosed()
                    }

                    override fun onBannerAutoRefreshed(info: ATAdInfo) {
                    }

                    override fun onBannerAutoRefreshFail(error: AdError) {
                    }

                })
            }
            bannerView?.loadAd()
        }
    }

    override fun show(container: ViewGroup, lp: ViewGroup.LayoutParams) {
        val adView = bannerView ?: return
        container.removeAllViews()
        if (adView.parent != null && adView.parent is ViewGroup) {
            (adView.parent as? ViewGroup)?.removeView(adView)
        }
        container.addView(adView, lp)
    }

    override fun isReady(): Boolean {
        return bannerView?.checkAdStatus()?.isReady == true
    }

    override fun getAdPlatform(): String {
        return ToponAdFactory.PLATFORM
    }

    override fun getAdType(): Int {
        return AdType.BANNER
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerView?.destroy()
        bannerView = null
    }
}