package com.android.atlasv.topon

import android.content.Context
import androidx.startup.Initializer
import com.android.atlasv.ad.framework.core.AdManager
import com.android.atlasv.ad.framework.core.IAdFactory

class ToponAdInitializer : Initializer<IAdFactory> {
    override fun create(context: Context): IAdFactory {
        AdManager.addSupportedFactory(ToponAdFactory)
        return ToponAdFactory
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }


}