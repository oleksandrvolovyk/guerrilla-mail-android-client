package volovyk.guerrillamail.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.remote.BackupRemoteEmailDatabase
import volovyk.guerrillamail.data.remote.MainRemoteEmailDatabase
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object EmailRepositoryModule {
    @Provides
    @Singleton
    fun provideEmailRepository(
        externalScope: CoroutineScope,
        @MainRemoteEmailDatabase mainRemoteEmailDatabase: RemoteEmailDatabase,
        @BackupRemoteEmailDatabase backupRemoteEmailDatabase: RemoteEmailDatabase,
        localEmailDatabase: LocalEmailDatabase
    ): EmailRepository {
        return EmailRepositoryImpl(
            externalScope,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase
        )
    }
}