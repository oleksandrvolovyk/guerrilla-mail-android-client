package volovyk.guerrillamail.data.remote.mailtm

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import retrofit2.Call
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.remote.exception.EmailAddressAssignmentException
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
    private var seenMessageIds: MutableSet<String> = mutableSetOf()

    override fun updateEmails() {
        if (token == null) throw NoEmailAddressAssignedException()

        // 1. Get all messages
        // TODO FIX: this does not fetch ALL messages(because of pagination)
        val getMessagesCall = mailTmApiInterface.getMessages(token = token!!)

        val listOfMessages = getMessagesCall.executeAndCatchErrors()

        // 2. Filter out already emitted messages

        val listOfNewMessages = listOfMessages.messages.filter { it.id !in seenMessageIds }

        // 3. Fetch each new message, add message ids to seenMessageIds

        val fullMessages = mutableListOf<Message>()

        listOfNewMessages.forEach { message ->
            val getMessageCall = mailTmApiInterface.getMessage(message.id, token = token!!)
            val fullMessage = getMessageCall.executeAndCatchErrors()

            fullMessages.add(fullMessage)
            seenMessageIds.add(fullMessage.id)
        }

        val fullEmails = fullMessages.map { message ->
            Email(
                id = Random.nextInt(),
                from = message.from.address,
                subject = message.subject,
                body = message.text ?: "",
                date = message.createdAt.toString()
            )
        }

        // 4. Update emails flow
        emails.update { fullEmails }
    }

    override fun hasEmailAddressAssigned(): Boolean = assignedEmail.value != null

    override fun getRandomEmailAddress() {
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
    }

    private fun generateRandomLatinString(length: Int): String {
        val charset = ('a'..'z') // Latin letters uppercase and lowercase
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    override fun setEmailAddress(requestedEmailAddress: String) {
        TODO("Not yet implemented")
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail

    override fun observeEmails(): Flow<List<Email>> = emails

    override fun observeState(): Flow<RemoteEmailDatabase.State> = state

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