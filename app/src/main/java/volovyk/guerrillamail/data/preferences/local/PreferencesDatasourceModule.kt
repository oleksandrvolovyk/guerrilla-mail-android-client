package volovyk.guerrillamail.data.preferences.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferencesDatasourceModule {
    @Provides
    @Singleton
    fun providePreferencesDatasource(@ApplicationContext appContext: Context): PreferencesDatasource {
        return PreferencesDataStore(appContext)
    }
}