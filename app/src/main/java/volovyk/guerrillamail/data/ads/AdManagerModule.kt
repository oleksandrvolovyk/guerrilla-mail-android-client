package volovyk.guerrillamail.data.ads

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AdManagerModule {
    @Provides
    @Singleton
    fun provideAdManager(): AdManager {
        return AdManagerImpl()
    }
}