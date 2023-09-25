package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import android.text.Html
import android.util.Base64
import com.fasterxml.jackson.annotation.JsonProperty
import volovyk.guerrillamail.data.emails.model.Email

class EmailGuerrillaMail(
    @JsonProperty("mail_id") val mailId: String,
    @JsonProperty("mail_from") val from: String,
    @JsonProperty("mail_subject") val subject: String,
    @JsonProperty("mail_body") val body: String,
    @JsonProperty("mail_date") val date: String,
)

fun EmailGuerrillaMail.toEmail() = Email(
    id = mailId,
    from = from,
    subject = subject,
    body = extractTextFromHtmlBody(body),
    htmlBody = encodeToBase64String(body),
    date = date,
    viewed = false
)

private fun extractTextFromHtmlBody(body: String): String {
    return Html.fromHtml(body, Html.FROM_HTML_MODE_COMPACT).toString()
}

private fun encodeToBase64String(input: String): String {
    return Base64.encodeToString(input.encodeToByteArray(), Base64.NO_PADDING)
}