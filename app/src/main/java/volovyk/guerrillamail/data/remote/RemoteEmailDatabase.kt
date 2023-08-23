package volovyk.guerrillamail.data.remote

import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.model.Email

interface RemoteEmailDatabase {

    fun updateEmails()
    fun hasEmailAddressAssigned(): Boolean
    fun getRandomEmailAddress()
    fun setEmailAddress(requestedEmailAddress: String)

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
    fun observeState(): Flow<State>

    sealed class State {
        object Success : State()
        object Loading : State()
        data class Failure(val error: Throwable) : State()
    }
}