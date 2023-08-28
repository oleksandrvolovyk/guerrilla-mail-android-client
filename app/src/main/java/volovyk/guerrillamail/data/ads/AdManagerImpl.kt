package volovyk.guerrillamail.data.ads

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import timber.log.Timber
import volovyk.guerrillamail.BuildConfig

class AdManagerImpl(private val appContext: Context) : AdManager {

    private val loadedAds = HashMap<Ad, InterstitialAd>()

    init {
        Timber.d("init")
        val requestConfiguration = MobileAds.getRequestConfiguration()
            .toBuilder()
            .setTagForChildDirectedTreatment(
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
            )
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        MobileAds.initialize(appContext)
    }

    override fun loadAd(ad: Ad) {
        if (!loadedAds.containsKey(ad)) { // If Ad is not already loaded
            Timber.d("Loading ad: $ad")
            if (ad == Ad.Interstitial) {
                val adRequest = AdRequest.Builder().build()

                val adId = if (BuildConfig.DEBUG) {
                    BuildConfig.ADMOB_TEST_AD_ID
                } else {
                    BuildConfig.ADMOB_MY_AD_ID
                }

                InterstitialAd.load(
                    appContext,
                    adId,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) = Unit

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Timber.d("Ad loaded: $ad")
                            loadedAds[ad] = interstitialAd

                            loadedAds[ad]!!.fullScreenContentCallback =
                                object : FullScreenContentCallback() {
                                    override fun onAdDismissedFullScreenContent() {
                                        Timber.d("Ad dismissed: $ad")
                                        loadedAds.remove(ad)
                                    }

                                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                        Timber.w("Ad failed to show: $ad")
                                        loadedAds.remove(ad)
                                    }
                                }
                        }
                    }
                )
            }
        }
    }

    override fun tryToShowAd(activity: Activity, ad: Ad) {
        loadedAds[ad]?.let {
            Timber.d("Showing ad: $ad")
            it.show(activity)
        }
    }
}