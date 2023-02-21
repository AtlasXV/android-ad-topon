package com.android.atlasv.topon

internal fun Int.getNetworkName(): String {
    return when (this) {
        1 -> "Meta"
        2 -> "Admob"
        3 -> "Inmobi"
        5 -> "Applovin"
        6 -> "Mintegral"
        13 -> "Vungle"
        50 -> "Pangle"
        else -> toString()
    }
}