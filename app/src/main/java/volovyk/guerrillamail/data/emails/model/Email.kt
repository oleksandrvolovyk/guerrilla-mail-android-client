package volovyk.guerrillamail.data.emails.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Email(
    @PrimaryKey val id: String,
    val from: String,
    val subject: String,
    val body: String,
    val htmlBody: String,
    val date: String,
    val viewed: Boolean
)