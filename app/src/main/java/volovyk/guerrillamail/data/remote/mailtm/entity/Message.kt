package volovyk.guerrillamail.data.remote.mailtm.entity

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