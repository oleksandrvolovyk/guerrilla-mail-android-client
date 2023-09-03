package volovyk.guerrillamail.data.remote.guerrillamail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import volovyk.guerrillamail.BuildConfig
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.remote.exception.EmailFetchException
import volovyk.guerrillamail.data.remote.exception.NoEmailAddressAssignedException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuerrillaEmailDatabase @Inject constructor(private val guerrillaMailApiInterface: GuerrillaMailApiInterface) :
    RemoteEmailDatabase {

    companion object {
        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    private val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    private val emails = MutableStateFlow(emptyList<Email>())
    private val state: MutableStateFlow<RemoteEmailDatabase.State> =
        MutableStateFlow(RemoteEmailDatabase.State.Loading)

    private var sidToken: String? = null
    private var seq = 0

    override fun isAvailable(): Boolean = try {
        val connection = URL(BuildConfig.GUERRILLAMAIL_API_BASE_URL).openConnection() as HttpURLConnection
        connection.connect()
        connection.disconnect()
        true
    } catch (e: IOException) {
        state.update { RemoteEmailDatabase.State.Failure(e) }
        false
    } catch (e: SocketTimeoutException) {
        state.update { RemoteEmailDatabase.State.Failure(e) }
        false
    }

    override fun updateEmails() {
        if (assignedEmail.value != null) {
            state.update { RemoteEmailDatabase.State.Loading }
            try {
                val emailsList = checkForNewEmails()
                emails.update { emailsList }
                state.update { RemoteEmailDatabase.State.Success }
            } catch (e: IOException) {
                state.update { RemoteEmailDatabase.State.Failure(EmailFetchException(e)) }
            }
        } else {
            throw NoEmailAddressAssignedException()
        }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() {
        state.update { RemoteEmailDatabase.State.Loading }
        try {
            val email = getEmailAddress()
            assignedEmail.update { email }
            state.update { RemoteEmailDatabase.State.Success }
        } catch (e: IOException) {
            state.update { RemoteEmailDatabase.State.Failure(EmailAddressAssignmentException(e)) }
        }
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        state.update { RemoteEmailDatabase.State.Loading }
        try {
            val assignedEmailAddress =
                makeSetEmailAddressRequest(requestedEmailAddress.substringBefore("@"))
            assignedEmail.update { assignedEmailAddress }
            state.update { RemoteEmailDatabase.State.Success }
        } catch (e: IOException) {
            state.update { RemoteEmailDatabase.State.Failure(EmailAddressAssignmentException(e)) }
        }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail
    override fun observeEmails(): Flow<List<Email>> = emails
    override fun observeState(): Flow<RemoteEmailDatabase.State> = state

    fun getSidToken() = sidToken
    fun getSeq() = seq

    private fun makeSetEmailAddressRequest(requestedEmailAddress: String): String {
        val call = guerrillaMailApiInterface
            .setEmailAddress(
                sidToken,
                LANG,
                SITE,
                requestedEmailAddress
            )

        val setEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = setEmailAddressResponse.sidToken
        seq = 0
        if (setEmailAddressResponse.emailAddress != null) {
            return setEmailAddressResponse.emailAddress
        } else {
            throw IOException("API did not return an email address")
        }
    }

    private fun getEmailAddress(): String {
        val call = guerrillaMailApiInterface.emailAddress

        val getEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = getEmailAddressResponse.sidToken
        if (getEmailAddressResponse.emailAddress != null) {
            return getEmailAddressResponse.emailAddress
        } else {
            throw IOException("API did not return an email address")
        }
    }

    private fun checkForNewEmails(): List<Email> {
        val call = guerrillaMailApiInterface.checkForNewEmails(sidToken, seq)

        val checkForNewEmailsResponse = call.executeAndCatchErrors()

        sidToken = checkForNewEmailsResponse.sidToken
        if (!checkForNewEmailsResponse.emails.isNullOrEmpty()) {
            return fetchAllEmails(checkForNewEmailsResponse.emails)
        }

        return emptyList()
    }

    private fun fetchAllEmails(emailsList: List<Email>): List<Email> {
        val fetchedEmailsList: MutableList<Email> = mutableListOf()
        for (email in emailsList) {
            val call = guerrillaMailApiInterface.fetchEmail(sidToken, email.id)

            val fetchedEmail = call.executeAndCatchErrors()

            fetchedEmailsList.add(fetchedEmail.copy(body = formatEmailBody(fetchedEmail.body)))
            seq = seq.coerceAtLeast(fetchedEmail.id)
        }
        return fetchedEmailsList
    }

    private fun formatEmailBody(body: String): String {
        return body.replace("\\r\\n".toRegex(), "<br>")
    }

    private fun <T> Call<T>.executeAndCatchErrors(): T {
        try {
            val response = this.execute()

            val responseBody = response.body()
            if (response.isSuccessful && responseBody != null) {
                return responseBody
            } else {
                throw IOException("Request was not successful or response body is null")
            }
        } catch (e: RuntimeException) {
            throw IOException(e)
        }
    }
}