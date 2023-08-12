package volovyk.guerrillamail.data.remote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import volovyk.guerrillamail.data.model.Email
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
    private val state: MutableStateFlow<RemoteEmailDatabase.State> =
        MutableStateFlow(RemoteEmailDatabase.State.Loading)

    private var sidToken: String? = null
    private var seq = 0

    override fun updateEmails() {
        if (assignedEmail.value != null) {
            state.update { RemoteEmailDatabase.State.Loading }
            try {
                val emailsList = checkForNewEmails()
                emails.update { emailsList }
                state.update { RemoteEmailDatabase.State.Success }
            } catch (e: RuntimeException) {
                state.update { RemoteEmailDatabase.State.Error }
            }
        } else {
            throw RuntimeException("It is not possible to check for new e-mails if no e-mail address is assigned")
        }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() {
        state.update { RemoteEmailDatabase.State.Loading }
        try {
            val email = getEmailAddress()
            assignedEmail.update { email }
            state.update { RemoteEmailDatabase.State.Success }
        } catch (e: RuntimeException) {
            state.update { RemoteEmailDatabase.State.Error }
        }
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        state.update { RemoteEmailDatabase.State.Loading }
        try {
            val assignedEmailAddress = makeSetEmailAddressRequest(requestedEmailAddress)
            assignedEmail.update { assignedEmailAddress }
            state.update { RemoteEmailDatabase.State.Success }
        } catch (e: RuntimeException) {
            state.update { RemoteEmailDatabase.State.Error }
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
            throw RuntimeException()
        }
    }

    private fun getEmailAddress(): String {
        val call = guerrillaMailApiInterface.emailAddress

        val getEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = getEmailAddressResponse.sidToken
        if (getEmailAddressResponse.emailAddress != null) {
            return getEmailAddressResponse.emailAddress
        } else {
            throw RuntimeException()
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

            val fullEmail = call.executeAndCatchErrors()

            fullEmail.body = formatEmailBody(fullEmail.body)
            fetchedEmailsList.add(fullEmail)
            seq = seq.coerceAtLeast(fullEmail.id)
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
                throw RuntimeException()
            }
        } catch (e: IOException) {
            throw RuntimeException()
        } catch (e: RuntimeException) {
            throw e
        }
    }
}