package volovyk.guerrillamail.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object ApiInterfaceModule {
    @Provides
    fun provideApiInterface() : ApiInterface {
        return ApiClient.client.create(ApiInterface::class.java)
    }
}