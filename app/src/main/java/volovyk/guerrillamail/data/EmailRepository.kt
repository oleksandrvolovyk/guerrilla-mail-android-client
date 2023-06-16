package volovyk.guerrillamail.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
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
class EmailRepository @Inject constructor(
    private val remoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase
) : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry
    val assignedEmail: LiveData<String?> = remoteEmailDatabase.assignedEmail
    val emails: Flow<List<Email>> = localEmailDatabase.getEmailDao().all
    val refreshing: LiveData<Boolean>
    val errorLiveData: LiveData<SingleEvent<String>>

    init {
        val remoteEmails = remoteEmailDatabase.emails
        refreshing = remoteEmailDatabase.refreshing
        errorLiveData = remoteEmailDatabase.errorLiveData
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleScope.launch {
            remoteEmails.collect { emails ->
                insertAllToLocalDatabase(emails)
            }
        }
    }

    suspend fun setEmailAddress(newAddress: String) {
        withContext(Dispatchers.IO) {
            remoteEmailDatabase.setEmailAddress(newAddress)
        }
    }

    suspend fun deleteEmail(email: Email?) {
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

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}