package volovyk.guerrillamail.ads

import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import volovyk.guerrillamail.data.ads.AdManager

object FakeAdManager : AdManager {
    override val ads: StateFlow<List<NativeAd>> = MutableStateFlow(emptyList())
    override suspend fun loadAd(position: Int) = Unit
    override fun destroyAllAds() = Unit
}