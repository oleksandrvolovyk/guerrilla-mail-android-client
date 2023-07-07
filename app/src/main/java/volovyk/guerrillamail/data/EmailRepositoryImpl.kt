package volovyk.guerrillamail.data

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailRepositoryImpl @Inject constructor (
    externalScope: CoroutineScope,
    private val remoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase
) : EmailRepository {
    override val assignedEmail: LiveData<String?> = remoteEmailDatabase.assignedEmail
    override val emails: Flow<List<Email>> = localEmailDatabase.getEmailDao().all
    override val refreshing: LiveData<Boolean> = remoteEmailDatabase.refreshing
    override val errorLiveData: LiveData<SingleEvent<String>> = remoteEmailDatabase.errorLiveData

    init {
        externalScope.launch {
            remoteEmailDatabase.emails.collect { emails ->
                insertAllToLocalDatabase(emails)
            }
        }
    }

    override suspend fun setEmailAddress(newAddress: String) {
        withContext(Dispatchers.IO) {
            remoteEmailDatabase.setEmailAddress(newAddress)
        }
    }

    override suspend fun deleteEmail(email: Email?) {
        withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().delete(email)
        }
    }

    // Must be called on a non-UI thread or Room will throw an exception.
    private suspend fun insertAllToLocalDatabase(emails: Collection<Email?>?) {
        withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }
    }
}