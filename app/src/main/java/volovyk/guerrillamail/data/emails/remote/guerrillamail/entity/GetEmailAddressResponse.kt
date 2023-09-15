package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty

class GetEmailAddressResponse(
    @JsonProperty("email_addr")
    val emailAddress: String,

    @JsonProperty("sid_token")
    val sidToken: String
)