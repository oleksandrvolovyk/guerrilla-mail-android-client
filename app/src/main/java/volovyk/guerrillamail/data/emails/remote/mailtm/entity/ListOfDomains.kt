package volovyk.guerrillamail.data.emails.remote.mailtm.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class ListOfDomains(
    @JsonProperty("hydra:member") val domains: List<Domain>,
    @JsonProperty("hydra:totalItems") val totalDomains: Int
)