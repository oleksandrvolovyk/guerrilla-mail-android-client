package volovyk.guerrillamail.data.emails.remote

import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.emails.model.Email

interface RemoteEmailDatabase {

    fun isAvailable(): Boolean

    fun updateEmails()
    fun hasEmailAddressAssigned(): Boolean
    fun getRandomEmailAddress()
    fun setEmailAddress(requestedEmailAddress: String): String

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
}