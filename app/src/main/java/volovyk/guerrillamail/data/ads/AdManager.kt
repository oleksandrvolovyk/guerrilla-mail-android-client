package volovyk.guerrillamail.data.ads

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.StateFlow

typealias Position = Int

interface AdManager {
    val ads: StateFlow<Map<Position, NativeAd>>
    suspend fun loadAd(position: Int)
    fun destroyAllAds()
}