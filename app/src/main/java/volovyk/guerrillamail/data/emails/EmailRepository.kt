package volovyk.guerrillamail.data.emails

import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase

interface EmailRepository {

    suspend fun getEmailById(emailId: String): Email?
    suspend fun setEmailAddress(newAddress: String)
    suspend fun deleteEmail(email: Email?)
    suspend fun deleteAllEmails()
    suspend fun retryConnectingToMainDatabase()

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
    fun observeState(): Flow<RemoteEmailDatabase.State>
    fun observeMainRemoteEmailDatabaseAvailability(): Flow<Boolean>
}