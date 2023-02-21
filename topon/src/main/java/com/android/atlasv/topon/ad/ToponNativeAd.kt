package com.android.atlasv.topon.ad

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.android.atlasv.ad.framework.ad.BaseAd
import com.android.atlasv.ad.framework.core.AdType
import com.android.atlasv.ad.framework.event.AnalysisEvent
import com.android.atlasv.ad.framework.event.EventAgent
import com.android.atlasv.ad.framework.util.AdLog
import com.android.atlasv.android.ad.framework.R
import com.android.atlasv.topon.ToponAdFactory
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.nativead.api.*
import com.bumptech.glide.Glide

class ToponNativeAd(val activity: Activity, private val adUnitId: String) : BaseAd() {
    companion object {
        private const val TAG = "ToponNativeAd"
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

    private var nativeAd: ATNative? = null
    private var nativeAdBean: NativeAd? = null

    override fun getAdPlatform(): String {
        return ToponAdFactory.PLATFORM
    }

    override fun getAdType(): Int {
        return AdType.NATIVE
    }

    override fun prepare() {
        super.prepare()
        AdLog.w(TAG) { "prepare $placement $adUnitId" }

        if (nativeAd == null) {
            nativeAd = ATNative(activity, adUnitId, object : ATNativeNetworkListener {
                override fun onNativeAdLoaded() {
                    AdLog.w(TAG) { "onNativeAdLoaded $placement $adUnitId" }
                    EventAgent.logEvent(
                        activity,
                        AnalysisEvent.AD_LOAD_SUCCESS, bundle
                    )
                    nativeAdBean = nativeAd?.nativeAd
                    adListener?.onAdLoaded(this@ToponNativeAd)
                }

                override fun onNativeAdLoadFail(error: AdError) {
                    AdLog.w(TAG) { "onNativeAdLoadFail.errorCode: ${error.code} $placement $adUnitId" }
                    val bundle = Bundle()
                    bundle.putString("unit_id", adUnitId)
                    bundle.putInt("errorCode", error.code.toInt())
                    EventAgent.logEvent(activity, AnalysisEvent.AD_LOAD_FAIL, bundle)

                    adListener?.onAdFailedToLoad(error.code.toInt())
                }

            })

            nativeAd?.makeAdRequest()
        }
    }

    override fun isReady(): Boolean {
        return nativeAd?.checkAdStatus()?.isReady == true
    }

    override fun show(container: ViewGroup, layoutId: Int): Boolean {
        val adBean = nativeAdBean
        ATNative.entryAdScenario(adUnitId, "");
        AdLog.w(TAG) { "show ad:${adBean != null} status:${nativeAd?.checkAdStatus()}" }

        if (adBean != null && isReady()) {

            val adContainer = LayoutInflater.from(activity).inflate(layoutId, container, false)
            adBean.setNativeEventListener(object : ATNativeEventListener {
                override fun onAdImpressed(ad: ATNativeAdView, info: ATAdInfo) {
                    AdLog.w(TAG) { "onAdImpressed $placement $adUnitId" }
                    adListener?.onAdImpression()
                    adListener?.onAdOpened()
                }

                override fun onAdClicked(ad: ATNativeAdView, info: ATAdInfo) {
                    AdLog.w(TAG) { "onAdClicked $placement $adUnitId" }
                    adListener?.onAdClicked()
                }

                override fun onAdVideoStart(ad: ATNativeAdView) {
                }

                override fun onAdVideoEnd(ad: ATNativeAdView) {
                }

                override fun onAdVideoProgress(ad: ATNativeAdView, p: Int) {
                }
            })
            renderAdContent(container, adContainer, adBean)
        }
        return true
    }

    private fun renderAdContent(container: ViewGroup, adView: View, adBean: NativeAd) {
        val ad = adBean.adMaterial
        val icon = adView.findViewById<FrameLayout>(R.id.icon)
        val title = adView.findViewById<TextView>(R.id.headline)
        val body = adView.findViewById<TextView>(R.id.body)
        val button = adView.findViewById<TextView>(R.id.callToAction)
        val mediaLayout = adView.findViewById<FrameLayout>(R.id.media)

        var iconView = ad.adIconView
        if (iconView == null) {
            val imgView = ImageView(activity)
            Glide.with(activity)
                .load(ad.iconImageUrl)
                .override(icon.width, icon.height)
                .into(imgView)
            iconView = imgView
        }
        icon.addView(
            iconView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        title.text = ad.title
        body.text = ad.descriptionText
        button.text = ad.callToActionText
        val mediaView = ad.getAdMediaView(mediaLayout)?.apply {
            mediaLayout.addView(
                this,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }


        val atNativeViewLayout = ATNativeAdView(activity)
        atNativeViewLayout.addView(
            adView,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        container.addView(
            atNativeViewLayout, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        val clickableViews = mutableListOf(
            icon, title, body, button, mediaView
        )
        val prepareInfo = ATNativePrepareInfo()
        prepareInfo.parentView = container
        prepareInfo.titleView = title
        prepareInfo.descView = body
        prepareInfo.clickViewList = clickableViews
        prepareInfo.iconView = icon
        prepareInfo.mainImageView = mediaLayout

        adBean.renderAdContainer(atNativeViewLayout, adView)
        adBean.prepare(atNativeViewLayout, prepareInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        nativeAdBean?.destory()
        nativeAdBean = null
    }
}