package volovyk.guerrillamail.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
    @Provides
    fun provideApiInterface(): ApiInterface {
        return ApiClient.client.create(ApiInterface::class.java)
    }

    @Provides
    fun provideRemoteEmailDatabase(apiInterface: ApiInterface): RemoteEmailDatabase {
        return GuerrillaEmailDatabase(apiInterface)
    }
}