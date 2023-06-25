package volovyk.guerrillamail.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
    @Provides
    @Singleton
    fun provideApiInterface(): ApiInterface {
        return ApiClient.client.create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteEmailDatabase(apiInterface: ApiInterface): RemoteEmailDatabase {
        return GuerrillaEmailDatabase(apiInterface)
    }
}