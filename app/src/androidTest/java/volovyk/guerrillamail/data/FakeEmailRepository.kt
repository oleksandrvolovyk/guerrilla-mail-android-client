package volovyk.guerrillamail.data

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.model.EmailRepositoryException

class FakeEmailRepository(
    initialAssignedEmail: String? = null,
    initialEmails: List<Email> = emptyList(),
    initialState: EmailRepository.State = EmailRepository.State(
        isLoading = false,
        isMainRemoteEmailDatabaseAvailable = true
    )
) : EmailRepository {

    val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(initialAssignedEmail)
    val emails = MutableStateFlow(initialEmails)
    val state: MutableStateFlow<EmailRepository.State> = MutableStateFlow(initialState)

    var isSetEmailAddressSuccessful = true

    override suspend fun getEmailById(emailId: String): Email? {
        return emails.value.find { it.id == emailId }
    }

    override suspend fun setEmailAddress(newAddress: String): String {
        return if (isSetEmailAddressSuccessful) {
            assignedEmail.update { newAddress }
            newAddress
        } else {
            throw EmailRepositoryException.EmailAddressAssignmentException(RuntimeException())
        }
    }

    override suspend fun deleteEmails(emailIds: List<String>) {
        emails.update { emails -> emails.filter { it.id !in emailIds } }
    }

    override suspend fun retryConnectingToMainDatabase() {
        state.update { it.copy(isMainRemoteEmailDatabaseAvailable = true) }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail
    override fun observeEmails(): Flow<List<Email>> = emails
    override fun observeState(): Flow<EmailRepository.State> = state
    override fun observeErrors(): ReceiveChannel<EmailRepositoryException> = Channel { }
}