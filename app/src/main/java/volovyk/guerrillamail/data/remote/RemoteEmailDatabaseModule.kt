package volovyk.guerrillamail.data.remote

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import volovyk.guerrillamail.data.remote.mailtm.MailTmApiInterface
import volovyk.guerrillamail.data.remote.mailtm.MailTmEmailDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RemoteEmailDatabaseModule {
//    @Provides
//    @Singleton
//    fun provideRemoteEmailDatabase(guerrillaMailApiInterface: GuerrillaMailApiInterface): RemoteEmailDatabase {
//        return GuerrillaEmailDatabase(guerrillaMailApiInterface)
//    }

    @Provides
    @Singleton
    fun provideRemoteEmailDatabase(mailTmEmailInterface: MailTmApiInterface): RemoteEmailDatabase {
        return MailTmEmailDatabase(mailTmEmailInterface)
    }
}