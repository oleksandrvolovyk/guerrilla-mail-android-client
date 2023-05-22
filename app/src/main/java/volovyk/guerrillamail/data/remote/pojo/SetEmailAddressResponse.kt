package volovyk.guerrillamail.data.remote.pojo

import com.google.gson.annotations.SerializedName

class SetEmailAddressResponse {
    @SerializedName("sid_token")
    val sidToken: String? = null

    @SerializedName("email_addr")
    val emailAddress: String? = null
}