package volovyk.guerrillamail.util

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import volovyk.guerrillamail.ui.util.EmailValidator
import volovyk.guerrillamail.ui.util.EmailValidatorImpl
import volovyk.guerrillamail.ui.util.UiUtilModule
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [UiUtilModule::class]
)
object FakeUtilModule {
    @Provides
    @Singleton
    fun provideEmailValidator(): EmailValidator {
        return EmailValidatorImpl()
    }
}