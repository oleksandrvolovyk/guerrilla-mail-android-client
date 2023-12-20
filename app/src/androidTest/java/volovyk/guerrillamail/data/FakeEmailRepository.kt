package volovyk.guerrillamail.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.util.State

class FakeEmailRepository(
    initialAssignedEmail: String? = null,
    initialEmails: List<Email> = emptyList(),
    initialState: State = State.Loading,
    initialMainRemoteEmailDatabaseAvailability: Boolean = true
) : EmailRepository {

    val assignedEmail: MutableStateFlow<String?> = MutableStateFlow(initialAssignedEmail)
    val emails = MutableStateFlow(initialEmails)
    val state: MutableStateFlow<State> =
        MutableStateFlow(initialState)
    val mainRemoteEmailDatabaseAvailability =
        MutableStateFlow(initialMainRemoteEmailDatabaseAvailability)

    var isSetEmailAddressSuccessful = true

    override suspend fun getEmailById(emailId: String): Email? {
        return emails.value.find { it.id == emailId }
    }

    override suspend fun setEmailAddress(newAddress: String): Boolean {
        return if (isSetEmailAddressSuccessful) {
            assignedEmail.update { newAddress }
            true
        } else {
            false
        }
    }

    override suspend fun deleteEmail(email: Email) {
        emails.update { emails ->
            emails.minus(email)
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

    override fun observeState(): Flow<State> = state

    override fun observeMainRemoteEmailDatabaseAvailability(): Flow<Boolean> =
        mainRemoteEmailDatabaseAvailability
}