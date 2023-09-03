package volovyk.guerrillamail.data.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty

class BriefEmail(
    @JsonProperty("mail_id") val id: Int,
    @JsonProperty("mail_from") val from: String,
    @JsonProperty("mail_subject") val subject: String,
    @JsonProperty("mail_date") val date: String
)