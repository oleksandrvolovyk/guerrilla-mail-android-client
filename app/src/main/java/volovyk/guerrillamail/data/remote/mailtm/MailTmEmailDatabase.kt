package volovyk.guerrillamail.data.remote.mailtm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.remote.exception.EmailAddressAssignmentException
import volovyk.guerrillamail.data.remote.exception.EmailFetchException
import volovyk.guerrillamail.data.remote.exception.NoEmailAddressAssignedException
import volovyk.guerrillamail.data.remote.mailtm.entity.AuthRequest
import volovyk.guerrillamail.data.remote.mailtm.entity.Message
import java.io.IOException
import kotlin.random.Random

class MailTmEmailDatabase(private val mailTmApiInterface: MailTmApiInterface) :
    RemoteEmailDatabase {

    companion object {
        private const val USERNAME_LENGTH = 8
        private const val PASSWORD_LENGTH = 12
    }

    private val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    private val emails = MutableStateFlow(emptyList<Email>())
    private val state: MutableStateFlow<RemoteEmailDatabase.State> =
        MutableStateFlow(RemoteEmailDatabase.State.Loading)

    private var token: String? = null

    override fun updateEmails() = try {
        state.update { RemoteEmailDatabase.State.Loading }
        if (token == null) throw NoEmailAddressAssignedException()

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

        val fullEmails = fullMessages.map { message ->
            Email(
                id = Random.nextInt(), // TODO: don't use Random to avoid overriding existing emails
                from = message.from.address,
                subject = message.subject,
                body = message.text ?: "",
                date = message.createdAt.toString()
            )
        }

        // 3. Update emails flow
        emails.update { fullEmails }
        state.update { RemoteEmailDatabase.State.Success }
    } catch (exception: IOException) {
        state.update { RemoteEmailDatabase.State.Failure(EmailFetchException(exception)) }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() = try {
        state.update { RemoteEmailDatabase.State.Loading }
        // 1. Get available domains
        val getDomainsCall = mailTmApiInterface.getDomains()

        val listOfDomains = getDomainsCall.executeAndCatchErrors()

        if (listOfDomains.totalDomains == 0) {
            throw EmailAddressAssignmentException(
                RuntimeException(
                    "No domains available"
                )
            )
        }

        // 2. Create an account with first available domain
        val domain = listOfDomains.domains[0].domain

        val username = generateRandomLatinString(USERNAME_LENGTH)
        val password = generateRandomLatinString(PASSWORD_LENGTH)
        val address = "$username@$domain"

        val createAccountCall = mailTmApiInterface.createAccount(AuthRequest(address, password))

        createAccountCall.executeAndCatchErrors()

        // 3. Login
        val loginCall = mailTmApiInterface.login(AuthRequest(address, password))

        val loginResponse = loginCall.executeAndCatchErrors()

        token = "Bearer ${loginResponse.token}"
        assignedEmail.update { address }
        state.update { RemoteEmailDatabase.State.Success }
    } catch (exception: IOException) {
        state.update { RemoteEmailDatabase.State.Failure(EmailAddressAssignmentException(exception)) }
    }

    private fun generateRandomLatinString(length: Int): String {
        val charset = ('a'..'z')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun setEmailAddress(requestedEmailAddress: String) = try {
        state.update { RemoteEmailDatabase.State.Loading }
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
        state.update { RemoteEmailDatabase.State.Success }
    } catch (exception: IOException) {
        state.update { RemoteEmailDatabase.State.Failure(EmailAddressAssignmentException(exception)) }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail

    override fun observeEmails(): Flow<List<Email>> = emails

    override fun observeState(): Flow<RemoteEmailDatabase.State> = state

    private fun <T> Call<T>.executeAndCatchErrors(checkForNullResponse: Boolean): T? {
        try {
            val response = this.execute()

            val responseBody = response.body()
            if (response.isSuccessful) {
                if (checkForNullResponse && responseBody == null) {
                    throw IOException("Response body is null")
                }
                return responseBody
            } else {
                throw IOException("Request was not successful")
            }
        } catch (e: RuntimeException) {
            throw IOException(e)
        }
    }

    private fun <T> Call<T>.executeAndCatchErrors(): T =
        executeAndCatchErrors(checkForNullResponse = true)!!
}