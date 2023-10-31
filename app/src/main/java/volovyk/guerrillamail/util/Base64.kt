package volovyk.guerrillamail.util

import kotlin.io.encoding.ExperimentalEncodingApi

object Base64 {
    @OptIn(ExperimentalEncodingApi::class)
    fun encodeToBase64String(input: String): String {
        return kotlin.io.encoding.Base64.encode(input.encodeToByteArray())
    }
}