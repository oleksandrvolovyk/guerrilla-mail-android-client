package volovyk.guerrillamail.data.ads

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

interface AdManager {
    val ads: StateFlow<List<NativeAd>>
    suspend fun loadAd(position: Int)
    fun destroyAllAds()
}