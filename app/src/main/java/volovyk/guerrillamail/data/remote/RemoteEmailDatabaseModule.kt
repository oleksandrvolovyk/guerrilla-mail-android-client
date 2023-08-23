package volovyk.guerrillamail.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.remote.guerrillamail.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.remote.guerrillamail.GuerrillaMailApiClient
import volovyk.guerrillamail.data.remote.guerrillamail.GuerrillaMailApiInterface
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
    @Provides
    @Singleton
    fun provideApiInterface(): GuerrillaMailApiInterface {
        return GuerrillaMailApiClient.client.create(GuerrillaMailApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteEmailDatabase(guerrillaMailApiInterface: GuerrillaMailApiInterface): RemoteEmailDatabase {
        return GuerrillaEmailDatabase(guerrillaMailApiInterface)
    }
}