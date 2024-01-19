package volovyk.guerrillamail.data.emails.remote.mailtm

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import volovyk.guerrillamail.BuildConfig

object MailTmApiClient {
    val client: Retrofit
        get() {
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
}