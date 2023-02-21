package com.android.atlasv.topon_demo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.android.atlasv.topon_demo.databinding.ActivityMainBinding
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoEventListener

class MainActivity : AppCompatActivity() {
    private val adSource = arrayOf(
        "All", "Facebook", "Admob", "Inmobi", "Applovin", "Mintegral", "Vungle", "Pangle"
    )
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AdHelper.build(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        AdHelper.getAd(Constants.INTERSTITIAL_AD_ID)?.prepare()

        binding.btnClean.setOnClickListener {
            binding.tvLog.text = ""
        }

        binding.btnShowInteraction.setOnClickListener {
            AdHelper.getAd(Constants.INTERSTITIAL_AD_ID)?.show(this)
        }

        binding.btnLoadBanner.setOnClickListener {
            AdHelper.getAd(Constants.BANNER_AD_ID)?.prepare()
        }

        binding.btnShowBanner.setOnClickListener {
            AdHelper.getAd(Constants.BANNER_AD_ID)?.show(
                binding.flyBanner, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }

        binding.btnLoadNative.setOnClickListener {
            AdHelper.getAd(Constants.NATIVE_AD_ID)?.prepare()
        }

        binding.btnShowNative.setOnClickListener {
            AdHelper.getAd(Constants.NATIVE_AD_ID)
                ?.show(binding.flyNative, R.layout.general_native_ad_layout)
        }
    }
}