package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaMailBodyUnfilter
import volovyk.guerrillamail.data.util.Base64Encoder
import volovyk.guerrillamail.data.util.HtmlTextExtractor

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
        textBody = htmlTextExtractor.extractText(body),
        filteredHtmlBody = base64Encoder.encodeToBase64String(body),
        fullHtmlBody = base64Encoder.encodeToBase64String(GuerrillaMailBodyUnfilter(body)),
        date = date,
        viewed = false
    )