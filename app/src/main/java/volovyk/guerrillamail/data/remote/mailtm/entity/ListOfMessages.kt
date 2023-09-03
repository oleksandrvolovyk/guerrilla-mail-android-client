package volovyk.guerrillamail.data.remote.mailtm.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class ListOfMessages(
    @JsonProperty("hydra:member") val messages: List<Message>,
    @JsonProperty("hydra:totalItems") val totalMessages: Int
)