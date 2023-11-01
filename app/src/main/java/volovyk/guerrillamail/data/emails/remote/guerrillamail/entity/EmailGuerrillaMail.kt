package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.util.Base64
import volovyk.guerrillamail.util.Html

class EmailGuerrillaMail(
    @JsonProperty("mail_id") val mailId: String,
    @JsonProperty("mail_from") val from: String,
    @JsonProperty("mail_subject") val subject: String,
    @JsonProperty("mail_body") val body: String,
    @JsonProperty("mail_date") val date: String
)

fun EmailGuerrillaMail.toEmail() = Email(
    id = mailId,
    from = from,
    subject = subject,
    body = Html.extractTextFromHtml(body),
    htmlBody = Base64.encodeToBase64String(body),
    date = date,
    viewed = false
)