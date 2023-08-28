package volovyk.guerrillamail.data.ads

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AdManagerModule {
    @Provides
    @Singleton
    fun provideAdManager(@ApplicationContext appContext: Context): AdManager {
        return AdManagerImpl(appContext)
    }
}