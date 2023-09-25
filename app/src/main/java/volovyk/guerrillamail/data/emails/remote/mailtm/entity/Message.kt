package volovyk.guerrillamail.data.emails.remote.mailtm.entity

import android.util.Base64
import volovyk.guerrillamail.data.emails.model.Email
import java.util.Date

data class Message(
    val id: String,
    val from: From,
    val text: String?,
    val html: List<String>?,
    val subject: String,
    val createdAt: Date
) {
    data class From(
        val address: String,
        val name: String?
    )
}

fun Message.toEmail() = Email(
    id = this.id,
    from = this.from.address,
    subject = this.subject,
    body = this.text ?: "",
    htmlBody = encodeToBase64String(this.html?.getOrNull(0) ?: ""),
    date = this.createdAt.toString(),
    viewed = false
)

private fun encodeToBase64String(input: String): String {
    return Base64.encodeToString(input.encodeToByteArray(), Base64.NO_PADDING)
}