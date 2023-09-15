package volovyk.guerrillamail.data.emails.remote.guerrillamail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import timber.log.Timber
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.emails.remote.exception.EmailFetchException
import volovyk.guerrillamail.data.emails.remote.exception.NoEmailAddressAssignedException
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.BriefEmail
import volovyk.guerrillamail.util.State
import java.io.IOException
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
    private val state: MutableStateFlow<State> =
        MutableStateFlow(State.Loading)

    private var sidToken: String? = null
    private var seq = 0

    override fun isAvailable(): Boolean = try {
        guerrillaMailApiInterface.ping().executeAndCatchErrors()
        true
    } catch (e: IOException) {
        state.update { State.Failure(e) }
        false
    }

    override fun updateEmails() {
        if (assignedEmail.value != null) {
            state.update { State.Loading }
            try {
                val emailsList = checkForNewEmails()
                emails.update { emailsList }
                state.update { State.Success }
            } catch (e: IOException) {
                Timber.e(e)
                state.update { State.Failure(EmailFetchException(e)) }
            }
        } else {
            throw NoEmailAddressAssignedException()
        }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() {
        state.update { State.Loading }
        try {
            val email = getEmailAddress()
            assignedEmail.update { email }
            state.update { State.Success }
        } catch (e: IOException) {
            Timber.e(e)
            state.update { State.Failure(EmailAddressAssignmentException(e)) }
        }
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        state.update { State.Loading }
        try {
            val assignedEmailAddress =
                makeSetEmailAddressRequest(requestedEmailAddress.substringBefore("@"))
            assignedEmail.update { assignedEmailAddress }
            state.update { State.Success }
        } catch (e: IOException) {
            Timber.e(e)
            state.update { State.Failure(EmailAddressAssignmentException(e)) }
        }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail
    override fun observeEmails(): Flow<List<Email>> = emails
    override fun observeState(): Flow<State> = state

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
        return setEmailAddressResponse.emailAddress
    }

    private fun getEmailAddress(): String {
        val call = guerrillaMailApiInterface.emailAddress

        val getEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = getEmailAddressResponse.sidToken
        return getEmailAddressResponse.emailAddress
    }

    private fun checkForNewEmails(): List<Email> {
        val call = guerrillaMailApiInterface.checkForNewEmails(sidToken, seq)

        val checkForNewEmailsResponse = call.executeAndCatchErrors()

        sidToken = checkForNewEmailsResponse.sidToken

        return fetchAllEmails(checkForNewEmailsResponse.emails)
    }

    private fun fetchAllEmails(emailsList: List<BriefEmail>): List<Email> {
        val fetchedEmailsList: MutableList<Email> = mutableListOf()
        for (email in emailsList) {
            val call = guerrillaMailApiInterface.fetchEmail(sidToken, email.id)

            val fetchedEmail = call.executeAndCatchErrors()

            fetchedEmailsList.add(fetchedEmail.copy(body = formatEmailBody(fetchedEmail.body)))
            seq = seq.coerceAtLeast(fetchedEmail.id.toInt())
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
            Timber.e(e)
            throw IOException(e)
        }
    }
}