package volovyk.guerrillamail.data.remote

import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.model.Email

interface RemoteEmailDatabase {

    fun setEmailAddress(requestedEmailAddress: String)

    fun observeAssignedEmail(): Flow<String?>
    fun observeEmails(): Flow<List<Email>>
    fun observeState(): Flow<State>

    enum class State {
        Success,
        Loading,
        Error
    }
}