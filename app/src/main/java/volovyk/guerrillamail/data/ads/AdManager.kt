package volovyk.guerrillamail.data.ads

import android.app.Activity

interface AdManager {
    fun loadAd(ad: Ad)
    fun tryToShowAd(activity: Activity, ad: Ad)
}

enum class Ad {
    Interstitial
}