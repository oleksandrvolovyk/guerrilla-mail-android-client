package volovyk.guerrillamail.data.emails

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import volovyk.guerrillamail.data.IoDispatcher
import volovyk.guerrillamail.data.emails.local.LocalEmailDatabase
import volovyk.guerrillamail.data.emails.remote.BackupRemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.MainRemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object EmailRepositoryModule {
    @Provides
    @Singleton
    fun provideEmailRepository(
        externalScope: CoroutineScope,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        @MainRemoteEmailDatabase mainRemoteEmailDatabase: RemoteEmailDatabase,
        @BackupRemoteEmailDatabase backupRemoteEmailDatabase: RemoteEmailDatabase,
        localEmailDatabase: LocalEmailDatabase,
        preferencesRepository: PreferencesRepository
    ): EmailRepository {
        return EmailRepositoryImpl(
            externalScope,
            ioDispatcher,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase,
            preferencesRepository
        )
    }
}