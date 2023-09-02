package volovyk.guerrillamail.data.remote.mailtm.entity

import com.google.gson.annotations.SerializedName

data class ListOfMessages(
    @SerializedName("hydra:member") val messages: List<Message>,
    @SerializedName("hydra:totalItems") val totalMessages: Int
)