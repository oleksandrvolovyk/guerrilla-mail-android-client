package volovyk.guerrillamail.data

import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase

interface EmailRepository {

    suspend fun getEmailById(emailId: Int): Email?
    suspend fun setEmailAddress(newAddress: String)
    suspend fun deleteEmail(email: Email?)

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
    fun observeState(): Flow<RemoteEmailDatabase.State>
}