package volovyk.guerrillamail.data.emails.remote.guerrillamail

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
annotation class GuerrillaMailRetrofit

@InstallIn(SingletonComponent::class)
@Module
object GuerrillaMailModule {
    @Provides
    @GuerrillaMailRetrofit
    @Singleton
    fun provideGuerrillaMailRetrofit(): Retrofit {
        val converterFactory = JacksonConverterFactory.create(
            ObjectMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).registerKotlinModule()
        )

        return Retrofit.Builder()
            .baseUrl(BuildConfig.GUERRILLAMAIL_API_BASE_URL)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiInterface(@GuerrillaMailRetrofit retrofit: Retrofit): GuerrillaMailApiInterface {
        return retrofit.create(GuerrillaMailApiInterface::class.java)
    }
}