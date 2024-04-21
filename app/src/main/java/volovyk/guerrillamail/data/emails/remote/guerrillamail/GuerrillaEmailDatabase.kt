package volovyk.guerrillamail.data.emails.remote.guerrillamail

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import timber.log.Timber
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.BriefEmail
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.toEmail
import volovyk.guerrillamail.data.emails.remote.model.RemoteEmailDatabaseException
import volovyk.guerrillamail.util.Base64Encoder
import volovyk.guerrillamail.util.HtmlTextExtractor
import javax.inject.Inject

class GuerrillaEmailDatabase @Inject constructor(
    private val guerrillaMailApiInterface: GuerrillaMailApiInterface,
    private val htmlTextExtractor: HtmlTextExtractor,
    private val base64Encoder: Base64Encoder
) : RemoteEmailDatabase {

    companion object {
        private const val SITE = "guerrillamail.com"
        private const val LANG = "en"
    }

    init {
        Timber.d("init ${hashCode()}")
    }

    private val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    private val emails = MutableStateFlow(emptyList<Email>())

    private var sidToken: String? = null
    private var seq = 0

    override fun isAvailable(): Boolean = try {
        guerrillaMailApiInterface.ping().executeAndCatchErrors()
        true
    } catch (e: RemoteEmailDatabaseException) {
        false
    }

    override fun updateEmails() {
        if (assignedEmail.value != null) {
            emails.update { checkForNewEmails() }
        } else {
            throw RemoteEmailDatabaseException.NoEmailAddressAssignedException
        }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() = assignedEmail.update { getEmailAddress() }

    override fun setEmailAddress(requestedEmailAddress: String): String {
        val assignedEmailAddress =
            makeSetEmailAddressRequest(requestedEmailAddress.substringBefore("@"))
        assignedEmail.update { assignedEmailAddress }
        return assignedEmailAddress
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail
    override fun observeEmails(): Flow<List<Email>> = emails

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

            fetchedEmailsList.add(fetchedEmail.toEmail(htmlTextExtractor, base64Encoder))
            seq = seq.coerceAtLeast(fetchedEmail.mailId.toInt())
        }
        return fetchedEmailsList
    }

    private fun <T> Call<T>.executeAndCatchErrors(): T {
        try {
            val response = this.execute()

            val responseBody = response.body()

            if (!response.isSuccessful)
                throw RemoteEmailDatabaseException.UnsuccessfulRequestException

            if (responseBody == null)
                throw RemoteEmailDatabaseException.EmptyResponseException

            return responseBody
        } catch (t: Throwable) {
            Timber.e(t)
            throw RemoteEmailDatabaseException(t)
        }
    }
}