package volovyk.guerrillamail.data.emails.remote.guerrillamail

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import volovyk.guerrillamail.BuildConfig

object GuerrillaMailApiClient {
    val client: Retrofit
        get() {
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
}