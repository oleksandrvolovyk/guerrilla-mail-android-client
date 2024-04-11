package volovyk.guerrillamail.util

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun provideHtmlTextExtractor(): HtmlTextExtractor {
        return AndroidHtmlTextExtractor()
    }

    @Provides
    @Singleton
    fun provideBase64Encoder(): Base64Encoder {
        return Base64EncoderImpl()
    }
}