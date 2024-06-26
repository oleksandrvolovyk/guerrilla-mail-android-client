package volovyk.guerrillamail.data.emails.remote.mailtm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import timber.log.Timber
import volovyk.guerrillamail.BuildConfig
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.AuthRequest
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.Message
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.toEmail
import volovyk.guerrillamail.data.emails.remote.model.RemoteEmailDatabaseException
import volovyk.guerrillamail.data.util.Base64Encoder
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MailTmEmailDatabase(
    private val mailTmApiInterface: MailTmApiInterface,
    private val base64Encoder: Base64Encoder
) : RemoteEmailDatabase {

    companion object {
        private const val USERNAME_LENGTH = 8
        private const val PASSWORD_LENGTH = 12
    }

    init {
        Timber.d("init ${hashCode()}")
    }

    private val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    private val emails = MutableStateFlow(emptyList<Email>())

    private var token: String? = null

    override fun isAvailable(): Boolean = try {
        val connection = URL(BuildConfig.MAILTM_API_BASE_URL).openConnection() as HttpURLConnection
        connection.connect()
        connection.disconnect()
        true
    } catch (e: IOException) {
        Timber.e(e)
        false
    } catch (e: SocketTimeoutException) {
        Timber.e(e)
        false
    }

    override fun updateEmails() {
        if (token == null) throw RemoteEmailDatabaseException.NoEmailAddressAssignedException

        // 1. Get all messages
        val getMessagesCall = mailTmApiInterface.getMessages(token = token!!)

        val listOfMessages = getMessagesCall.executeAndCatchErrors()

        // 2. Fetch each new message, delete message after receiving
        val fullMessages = mutableListOf<Message>()

        listOfMessages.messages.forEach { message ->
            val getMessageCall = mailTmApiInterface.getMessage(message.id, token = token!!)
            val fullMessage = getMessageCall.executeAndCatchErrors()

            fullMessages.add(fullMessage)

            val deleteMessageCall = mailTmApiInterface.deleteMessage(message.id, token = token!!)
            deleteMessageCall.executeAndCatchErrors(checkForNullResponse = false)
        }

        val fullEmails = fullMessages.map { it.toEmail(base64Encoder) }

        // 3. Update emails flow
        emails.update { fullEmails }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() {
        // 1. Get available domains
        val getDomainsCall = mailTmApiInterface.getDomains()

        val listOfDomains = getDomainsCall.executeAndCatchErrors()

        if (listOfDomains.totalDomains == 0) {
            throw RemoteEmailDatabaseException("No domains available")
        }

        // 2. Create an account with first available domain
        val domain = listOfDomains.domains[0].domain

        val username = generateRandomLatinString(USERNAME_LENGTH)
        val address = "$username@$domain"

        setEmailAddress(address)
    }

    private fun generateRandomLatinString(length: Int): String {
        val charset = ('a'..'z')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun setEmailAddress(requestedEmailAddress: String): String {
        val requestEmailAddress = requestedEmailAddress.lowercase()

        // 1. Create an account with random password
        val password = generateRandomLatinString(PASSWORD_LENGTH)

        val createAccountCall =
            mailTmApiInterface.createAccount(AuthRequest(requestEmailAddress, password))

        createAccountCall.executeAndCatchErrors()

        // 2. Login
        val loginCall = mailTmApiInterface.login(AuthRequest(requestEmailAddress, password))

        val loginResponse = loginCall.executeAndCatchErrors()

        token = "Bearer ${loginResponse.token}"
        assignedEmail.update { requestedEmailAddress }
        return requestedEmailAddress
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail

    override fun observeEmails(): Flow<List<Email>> = emails

    private fun <T> Call<T>.executeAndCatchErrors(checkForNullResponse: Boolean): T? {
        try {
            val response = this.execute()

            val responseBody = response.body()

            if (!response.isSuccessful)
                throw RemoteEmailDatabaseException.UnsuccessfulRequestException

            if (checkForNullResponse && responseBody == null)
                throw RemoteEmailDatabaseException.EmptyResponseException

            return responseBody
        } catch (t: Throwable) {
            Timber.e(t)
            throw RemoteEmailDatabaseException(t)
        }
    }

    private fun <T> Call<T>.executeAndCatchErrors(): T =
        executeAndCatchErrors(checkForNullResponse = true)!!
}