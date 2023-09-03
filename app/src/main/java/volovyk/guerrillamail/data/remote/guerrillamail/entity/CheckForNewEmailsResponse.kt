package volovyk.guerrillamail.data.remote.guerrillamail.entity

import com.fasterxml.jackson.annotation.JsonProperty

class CheckForNewEmailsResponse(
    @JsonProperty("list")
    val emails: List<BriefEmail>,

    @JsonProperty("sid_token")
    val sidToken: String
)