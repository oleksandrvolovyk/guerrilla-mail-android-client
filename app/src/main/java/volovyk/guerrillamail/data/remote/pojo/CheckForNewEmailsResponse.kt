package volovyk.guerrillamail.data.remote.pojo

import com.google.gson.annotations.SerializedName
import volovyk.guerrillamail.data.model.Email

class CheckForNewEmailsResponse {
    @SerializedName("list")
    val emails: List<Email>? = null

    @SerializedName("sid_token")
    val sidToken: String? = null
}