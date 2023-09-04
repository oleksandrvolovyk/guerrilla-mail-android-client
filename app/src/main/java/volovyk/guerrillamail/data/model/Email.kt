package volovyk.guerrillamail.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fasterxml.jackson.annotation.JsonProperty

@Entity
data class Email(
    @JsonProperty("mail_id") @PrimaryKey val id: String,
    @JsonProperty("mail_from") val from: String,
    @JsonProperty("mail_subject") val subject: String,
    @JsonProperty("mail_body") val body: String,
    @JsonProperty("mail_date") val date: String,
    val viewed: Boolean = false
)