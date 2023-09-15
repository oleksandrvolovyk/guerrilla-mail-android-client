package volovyk.guerrillamail.data.emails.remote.mailtm.entity

import volovyk.guerrillamail.data.emails.model.Email
import java.util.Date

data class Message(
    val id: String,
    val from: From,
    val text: String?,
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
    date = this.createdAt.toString()
)