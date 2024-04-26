package volovyk.guerrillamail.ui.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object UiUtilModule {
    @Provides
    @Singleton
    fun provideEmailValidator(): EmailValidator {
        return EmailValidatorImpl()
    }
}