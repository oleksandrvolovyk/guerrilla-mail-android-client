package volovyk.guerrillamail.data.preferences

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.preferences.local.PreferencesDatasource
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesRepositoryModule {
    @Provides
    @Singleton
    fun providePreferencesRepository(preferencesDatasource: PreferencesDatasource): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesDatasource)
    }
}