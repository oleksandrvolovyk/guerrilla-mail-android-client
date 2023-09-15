package volovyk.guerrillamail.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase

class FakeEmailRepository : EmailRepository {

    val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(null)
    val emails = MutableStateFlow(emptyList<Email>())
    val state: MutableStateFlow<RemoteEmailDatabase.State> =
        MutableStateFlow(RemoteEmailDatabase.State.Loading)
    val mainRemoteEmailDatabaseAvailability = MutableStateFlow(true)

    override suspend fun getEmailById(emailId: String): Email? {
        return emails.value.find { it.id == emailId }
    }

    override suspend fun setEmailAddress(newAddress: String) {
        assignedEmail.update { newAddress }
    }

    override suspend fun deleteEmail(email: Email?) {
        email?.let {
            emails.update { emails ->
                emails.minus(email)
            }
        }
    }

    override suspend fun deleteAllEmails() {
        emails.update { emptyList() }
    }

    override suspend fun retryConnectingToMainDatabase() {
        mainRemoteEmailDatabaseAvailability.update { true }
    }

    override fun observeAssignedEmail(): Flow<String?> = assignedEmail

    override fun observeEmails(): Flow<List<Email>> = emails

    override fun observeState(): Flow<RemoteEmailDatabase.State> = state

    override fun observeMainRemoteEmailDatabaseAvailability(): Flow<Boolean> =
        mainRemoteEmailDatabaseAvailability
}