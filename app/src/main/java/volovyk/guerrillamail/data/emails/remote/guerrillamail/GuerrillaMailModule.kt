package volovyk.guerrillamail.data.emails.remote.guerrillamail

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GuerrillaMailModule {
    @Provides
    @Singleton
    fun provideApiInterface(): GuerrillaMailApiInterface {
        return GuerrillaMailApiClient.client.create(GuerrillaMailApiInterface::class.java)
    }
}