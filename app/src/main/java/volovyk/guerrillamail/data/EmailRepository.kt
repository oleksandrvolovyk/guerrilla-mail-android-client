package volovyk.guerrillamail.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import volovyk.guerrillamail.data.model.Email

interface EmailRepository {
    val assignedEmail: LiveData<String?>
    val emails: Flow<List<Email>>
    val refreshing: LiveData<Boolean>
    val errorLiveData: LiveData<SingleEvent<String>>

    suspend fun setEmailAddress(newAddress: String)
    suspend fun deleteEmail(email: Email?)
}