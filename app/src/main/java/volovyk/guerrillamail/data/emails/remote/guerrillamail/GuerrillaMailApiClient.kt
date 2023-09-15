package volovyk.guerrillamail.data.emails.remote.guerrillamail

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import volovyk.guerrillamail.BuildConfig

object GuerrillaMailApiClient {
    val client: Retrofit
        get() {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BASIC
            val converterFactory = JacksonConverterFactory.create(
                ObjectMapper().configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false
                ).registerKotlinModule()
            )
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            return Retrofit.Builder()
                .baseUrl(BuildConfig.GUERRILLAMAIL_API_BASE_URL)
                .addConverterFactory(converterFactory)
                .client(client)
                .build()
        }
}