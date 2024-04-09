package volovyk.guerrillamail.data.emails.remote.mailtm

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import volovyk.guerrillamail.BuildConfig
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MailTmRetrofit

@InstallIn(SingletonComponent::class)
@Module
object MailTmModule {
    @Provides
    @MailTmRetrofit
    @Singleton
    fun provideMailTmRetrofit(): Retrofit {
        val converterFactory = JacksonConverterFactory.create(
            ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).registerKotlinModule()
        )

        return Retrofit.Builder()
            .baseUrl(BuildConfig.MAILTM_API_BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(@MailTmRetrofit retrofit: Retrofit): MailTmApiInterface {
        return retrofit.create(MailTmApiInterface::class.java)
    }
}