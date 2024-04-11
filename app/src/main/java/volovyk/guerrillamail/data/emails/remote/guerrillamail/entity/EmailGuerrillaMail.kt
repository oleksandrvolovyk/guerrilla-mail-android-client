package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.util.Base64Encoder
import volovyk.guerrillamail.util.HtmlTextExtractor

class EmailGuerrillaMail(
    @JsonProperty("mail_id") val mailId: String,
    @JsonProperty("mail_from") val from: String,
    @JsonProperty("mail_subject") val subject: String,
    @JsonProperty("mail_body") val body: String,
    @JsonProperty("mail_date") val date: String
)

fun EmailGuerrillaMail.toEmail(htmlTextExtractor: HtmlTextExtractor, base64Encoder: Base64Encoder) =
    Email(
        id = mailId,
        from = from,
        subject = subject,
        body = htmlTextExtractor.extractText(body),
        htmlBody = base64Encoder.encodeToBase64String(body),
        date = date,
        viewed = false
    )