package volovyk.guerrillamail.util

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UtilModule::class]
)
object FakeUtilModule {
    @Provides
    @Singleton
    fun provideEmailValidator(): EmailValidator {
        return EmailValidatorImpl()
    }

    @Provides
    @Singleton
    fun provideMessageHandler(): MessageHandler {
        return FakeMessageHandler()
    }
}