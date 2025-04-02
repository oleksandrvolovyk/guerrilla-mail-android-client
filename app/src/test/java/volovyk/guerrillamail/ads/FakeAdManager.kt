package volovyk.guerrillamail.ads

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import volovyk.guerrillamail.data.ads.AdManager
import volovyk.guerrillamail.data.ads.Position

object FakeAdManager : AdManager {
    override val ads: StateFlow<Map<Position, NativeAd>> = MutableStateFlow(emptyMap())
    override suspend fun loadAd(position: Int) = Unit
    override fun destroyAllAds() = Unit
}