package volovyk.guerrillamail.data.remote

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.SingleEvent
import volovyk.guerrillamail.data.model.Email

interface RemoteEmailDatabase {
    val assignedEmail: LiveData<String?>
    val emails: Flow<List<Email>>
    val refreshing: LiveData<Boolean>
    val errorLiveData: LiveData<SingleEvent<String>>

    fun setEmailAddress(requestedEmailAddress: String)
}