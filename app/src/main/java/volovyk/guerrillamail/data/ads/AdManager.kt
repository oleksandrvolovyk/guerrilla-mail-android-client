package volovyk.guerrillamail.data.ads

import android.app.Activity
import android.content.Context

interface AdManager {
    fun initialize(context: Context)
    fun loadAd(context: Context, ad: Ad)
    fun tryToShowAd(activity: Activity, ad: Ad)
}

enum class Ad {
    Interstitial
}