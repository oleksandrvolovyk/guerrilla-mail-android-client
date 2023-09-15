package volovyk.guerrillamail.data.emails.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaMailApiInterface
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmApiInterface
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmEmailDatabase
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
    @Provides
    @MainRemoteEmailDatabase
    @Singleton
    fun provideMainRemoteEmailDatabase(guerrillaMailApiInterface: GuerrillaMailApiInterface): RemoteEmailDatabase {
        return GuerrillaEmailDatabase(guerrillaMailApiInterface)
    }

    @Provides
    @BackupRemoteEmailDatabase
    @Singleton
    fun provideBackupRemoteEmailDatabase(mailTmEmailInterface: MailTmApiInterface): RemoteEmailDatabase {
        return MailTmEmailDatabase(mailTmEmailInterface)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainRemoteEmailDatabase

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackupRemoteEmailDatabase
