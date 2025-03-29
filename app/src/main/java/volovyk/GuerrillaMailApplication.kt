package volovyk

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import volovyk.guerrillamail.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@HiltAndroidApp
class GuerrillaMailApplication : Application() {

    @Inject
    lateinit var backgroundScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("onCreate")
        }
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@GuerrillaMailApplication) {}
        }
    }
}

@InstallIn(SingletonComponent::class)
@Module
object GuerrillaMailApplicationModule {
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob())
    }
}