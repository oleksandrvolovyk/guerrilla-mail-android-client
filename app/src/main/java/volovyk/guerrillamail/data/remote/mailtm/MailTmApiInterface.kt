package volovyk.guerrillamail.data.remote.mailtm

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import volovyk.guerrillamail.data.remote.mailtm.entity.AuthRequest
import volovyk.guerrillamail.data.remote.mailtm.entity.ListOfDomains
import volovyk.guerrillamail.data.remote.mailtm.entity.ListOfMessages
import volovyk.guerrillamail.data.remote.mailtm.entity.LoginResponse
import volovyk.guerrillamail.data.remote.mailtm.entity.Message

interface MailTmApiInterface {
    @GET("domains")
    fun getDomains(@Query("page") page: Int = 1): Call<ListOfDomains>

    @POST("accounts")
    fun createAccount(@Body request: AuthRequest): Call<Unit>

    @POST("token")
    fun login(@Body request: AuthRequest): Call<LoginResponse>

    @GET("messages")
    fun getMessages(
        @Query("page") page: Int = 1,
        @Header("Authorization") token: String
    ): Call<ListOfMessages>

    @GET("messages/{id}")
    fun getMessage(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Header("Authorization") token: String
    ): Call<Message>

    @DELETE("messages/{id}")
    fun deleteMessage(
        @Path("id") id: String,
        @Header("Authorization") token: String
    ): Call<Unit>
}