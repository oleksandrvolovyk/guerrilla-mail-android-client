package volovyk.guerrillamail.util

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UtilModule {
    @Provides
    @Singleton
    fun provideEmailValidator(): EmailValidator {
        return EmailValidatorImpl()
    }

    @Provides
    @Singleton
    fun provideMessageHandler(@ApplicationContext appContext: Context): MessageHandler {
        return MessageHandlerImpl(appContext)
    }
}