package volovyk.guerrillamail.data.preferences

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import volovyk.guerrillamail.data.IoDispatcher
import volovyk.guerrillamail.data.preferences.local.PreferencesDatasource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesRepositoryModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        preferencesDatasource: PreferencesDatasource
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(ioDispatcher, preferencesDatasource)
    }
}