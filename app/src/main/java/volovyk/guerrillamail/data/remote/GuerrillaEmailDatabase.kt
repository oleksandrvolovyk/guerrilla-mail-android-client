package volovyk.guerrillamail.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import retrofit2.Call
import volovyk.guerrillamail.data.model.Email
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GuerrillaEmailDatabase @Inject constructor(private val guerrillaMailApiInterface: GuerrillaMailApiInterface) :
    RemoteEmailDatabase {

    private val assignedEmail: MutableStateFlow<String?> = MutableStateFlow("")

    private val emails: Flow<List<Email>> = flow {
        while (true) {
            if (!gotEmailAssigned) {
                state.update { RemoteEmailDatabase.State.Loading }
                try {
                    val email = getEmailAddress()
                    assignedEmail.update { email }
                    if (email != null) {
                        gotEmailAssigned = true
                        state.update { RemoteEmailDatabase.State.Success }
                    }
                } catch (e: RuntimeException) {
                    state.update { RemoteEmailDatabase.State.Error }
                }
                delay(EMAIL_ASSIGNMENT_INTERVAL) // Suspends the coroutine for some time
            } else {
                state.update { RemoteEmailDatabase.State.Loading }
                try {
                    val emailsList = checkForNewEmails()
                    emit(emailsList)
                    state.update { RemoteEmailDatabase.State.Success }
                } catch (e: RuntimeException) {
                    state.update { RemoteEmailDatabase.State.Error }
                }
                delay(REFRESH_INTERVAL) // Suspends the coroutine for some time
            }
        }
    }.flowOn(Dispatchers.IO)

    private val state: MutableStateFlow<RemoteEmailDatabase.State> =
        MutableStateFlow(RemoteEmailDatabase.State.Loading)

    private var sidToken: String? = null
    private var seq = 0

    fun getSidToken() = sidToken

    fun getSeq() = seq

    private var gotEmailAssigned = false

    companion object {
        private const val REFRESH_INTERVAL = 5000L // 5 seconds
        private const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts

        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        val assignedEmailAddress = makeSetEmailAddressRequest(requestedEmailAddress)

        assignedEmail.update { assignedEmailAddress }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail
    override fun observeEmails(): Flow<List<Email>> = emails
    override fun observeState(): Flow<RemoteEmailDatabase.State> = state

    private fun makeSetEmailAddressRequest(requestedEmailAddress: String): String? {
        val call = guerrillaMailApiInterface
            .setEmailAddress(
                sidToken,
                LANG,
                SITE,
                requestedEmailAddress
            )

        val setEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = setEmailAddressResponse.sidToken
        return setEmailAddressResponse.emailAddress
    }

    private fun getEmailAddress(): String? {
        val call = guerrillaMailApiInterface.emailAddress

        val getEmailAddressResponse = call.executeAndCatchErrors()

        sidToken = getEmailAddressResponse.sidToken
        return getEmailAddressResponse.emailAddress
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