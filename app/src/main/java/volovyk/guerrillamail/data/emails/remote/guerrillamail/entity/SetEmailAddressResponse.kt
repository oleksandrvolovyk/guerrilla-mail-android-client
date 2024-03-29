package volovyk.guerrillamail.data.emails.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty

class SetEmailAddressResponse(
    @JsonProperty("email_addr")
    val emailAddress: String,

    @JsonProperty("sid_token")
    val sidToken: String
)