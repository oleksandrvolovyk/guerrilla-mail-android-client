package volovyk.guerrillamail.data.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import volovyk.guerrillamail.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdManagerImpl(private val context: Context) : AdManager {
    private val _adCache = MutableStateFlow(mapOf<Position, NativeAd>())
    override val ads = _adCache.asStateFlow()
    private val adLoaderMutex = Mutex()

    override suspend fun loadAd(position: Int): Unit = withContext(Dispatchers.Main) {
        adLoaderMutex.withLock {
            if (_adCache.value[position] != null) return@withContext

            val ad = suspendCoroutine { continuation ->
                AdLoader.Builder(context, BuildConfig.ADMOB_NATIVE_AD_ID)
                    .forNativeAd { ad: NativeAd ->
                        continuation.resume(ad)
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Timber.e("AdMob Ad failed to load: ${loadAdError.message}")
                            continuation.resume(null)
                        }
                    })
                    .build()
                    .loadAd(AdRequest.Builder().build())
            }

            if (ad != null) {
                _adCache.update { it.toMutableMap().apply { set(position, ad) } }
            }
        }
    }

    override fun destroyAllAds() {
        _adCache.value.values.forEach { it.destroy() }
        _adCache.update { emptyMap() }
    }
}