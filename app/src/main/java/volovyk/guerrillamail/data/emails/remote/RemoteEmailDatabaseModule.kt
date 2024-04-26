package volovyk.guerrillamail.data.emails.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaMailApiInterface
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmApiInterface
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmEmailDatabase
import volovyk.guerrillamail.data.util.Base64Encoder
import volovyk.guerrillamail.data.util.HtmlTextExtractor
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
    @Provides
    @MainRemoteEmailDatabase
    @Singleton
    fun provideMainRemoteEmailDatabase(
        guerrillaMailApiInterface: GuerrillaMailApiInterface,
        htmlTextExtractor: HtmlTextExtractor,
        base64Encoder: Base64Encoder
    ): RemoteEmailDatabase {
        return GuerrillaEmailDatabase(guerrillaMailApiInterface, htmlTextExtractor, base64Encoder)
    }

    @Provides
    @BackupRemoteEmailDatabase
    @Singleton
    fun provideBackupRemoteEmailDatabase(
        mailTmEmailInterface: MailTmApiInterface,
        base64Encoder: Base64Encoder
    ): RemoteEmailDatabase {
        return MailTmEmailDatabase(mailTmEmailInterface, base64Encoder)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRemoteEmailDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackupRemoteEmailDatabase