package volovyk.guerrillamail.data.remote

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse

interface GuerrillaMailApiInterface {
    @get:GET("ajax.php?f=get_email_address")
    val emailAddress: Call<GetEmailAddressResponse>

    @GET("ajax.php?f=check_email")
    fun checkForNewEmails(
        @Query("sid_token") sidToken: String?,
        @Query("seq") seq: Int
    ): Call<CheckForNewEmailsResponse>

    @GET("ajax.php?f=fetch_email")
    fun fetchEmail(
        @Query("sid_token") sidToken: String?,
        @Query("email_id") id: Int
    ): Call<Email>

    @GET("ajax.php?f=set_email_user")
    fun setEmailAddress(
        @Query("sid_token") sidToken: String?,
        @Query("lang") lang: String,
        @Query("site") site: String,
        @Query("email_user") newAddress: String
    ): Call<SetEmailAddressResponse>
}