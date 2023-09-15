package volovyk.guerrillamail.data.emails.remote.mailtm

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MailTmModule {
    @Provides
    @Singleton
    fun provideApiInterface(): MailTmApiInterface {
        return MailTmApiClient.client.create(MailTmApiInterface::class.java)
    }
}