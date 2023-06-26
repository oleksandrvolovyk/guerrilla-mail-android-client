package volovyk.guerrillamail.data

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object EmailRepositoryModule {
    @Provides
    @Singleton
    fun provideEmailRepository(
        remoteEmailDatabase: RemoteEmailDatabase,
        localEmailDatabase: LocalEmailDatabase
    ): EmailRepository {
        return EmailRepositoryImpl(remoteEmailDatabase, localEmailDatabase)
    }
}