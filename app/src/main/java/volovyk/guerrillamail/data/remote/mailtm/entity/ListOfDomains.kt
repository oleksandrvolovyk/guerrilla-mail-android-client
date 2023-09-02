package volovyk.guerrillamail.data.remote.mailtm.entity

import com.google.gson.annotations.SerializedName

data class ListOfDomains(
    @SerializedName("hydra:member") val domains: List<Domain>,
    @SerializedName("hydra:totalItems") val totalDomains: Int
)