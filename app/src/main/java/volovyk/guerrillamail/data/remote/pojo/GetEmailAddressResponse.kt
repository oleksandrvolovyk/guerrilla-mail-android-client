package volovyk.guerrillamail.data.remote.pojo

import com.google.gson.annotations.SerializedName

class GetEmailAddressResponse {
    @SerializedName("email_addr")
    val emailAddress: String? = null

    @SerializedName("sid_token")
    val sidToken: String? = null
}