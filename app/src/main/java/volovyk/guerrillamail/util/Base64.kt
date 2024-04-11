package volovyk.guerrillamail.util

import kotlin.io.encoding.ExperimentalEncodingApi

interface Base64Encoder {
    fun encodeToBase64String(input: String): String
}

class Base64EncoderImpl : Base64Encoder {
    @OptIn(ExperimentalEncodingApi::class)
    override fun encodeToBase64String(input: String): String {
        return kotlin.io.encoding.Base64.encode(input.encodeToByteArray())
    }
}