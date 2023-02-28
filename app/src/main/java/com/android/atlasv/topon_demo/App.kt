package com.android.atlasv.topon_demo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import android.util.Log
import com.android.atlasv.ad.framework.core.AdManager
import com.android.atlasv.ad.framework.util.AdLog
import com.anythink.core.api.ATDebuggerConfig
import com.anythink.core.api.ATSDK
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialAutoAd
import com.anythink.interstitial.api.ATInterstitialAutoLoadListener

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!isMainProcess(this)) {
            return
        }
        if (BuildConfig.DEBUG) {
            AdLog.setLogLevel(Log.VERBOSE)
        }
//        ATSDK.setDebuggerConfig(
//            this, "5d77d58d17b69dcb",
//            ATDebuggerConfig.Builder(6)
//                .build()
//        )
        AdManager.initializePlatformSdk(this)
        AdHelper.init(this)
    }

    private fun isMainProcess(context: Context?): Boolean {
        try {
            if (null != context) {
                return context.packageName == getProcessName(context)
            }
        } catch (e: Exception) {
            return false
        }
        return true
    }

    private fun getProcessName(cxt: Context): String? {
        val pid = Process.myPid()
        val am = cxt.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val runningApps = am.runningAppProcesses ?: return null
        for (procInfo in runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName
            }
        }
        return null
    }

}