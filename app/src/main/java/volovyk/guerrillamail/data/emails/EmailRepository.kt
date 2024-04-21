package volovyk.guerrillamail.data.emails

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.model.EmailRepositoryException

interface EmailRepository {

    suspend fun getEmailById(emailId: String): Email?
    suspend fun setEmailAddress(newAddress: String): String
    suspend fun deleteEmails(emailIds: List<String>)
    suspend fun retryConnectingToMainDatabase()

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
    fun observeState(): Flow<State>
    fun observeErrors(): ReceiveChannel<EmailRepositoryException>

    data class State(
        val isLoading: Boolean,
        val isMainRemoteEmailDatabaseAvailable: Boolean
    )
}