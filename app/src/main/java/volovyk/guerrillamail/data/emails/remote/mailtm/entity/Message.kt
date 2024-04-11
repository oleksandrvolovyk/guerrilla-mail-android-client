package volovyk.guerrillamail.data.emails.remote.mailtm.entity

import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.util.Base64Encoder
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

fun Message.toEmail(base64Encoder: Base64Encoder) = Email(
    id = this.id,
    from = this.from.address,
    subject = this.subject,
    body = this.text ?: "",
    htmlBody = base64Encoder.encodeToBase64String(this.html?.getOrNull(0) ?: ""),
    date = this.createdAt.toString(),
    viewed = false
)