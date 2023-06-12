package volovyk.guerrillamail.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.local.RoomEmailDatabase
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

    fun setEmailAddress(newAddress: String) {
        remoteEmailDatabase.setEmailAddress(newAddress)
    }

    fun deleteEmail(email: Email?) {
        RoomEmailDatabase.databaseExecutorService.execute {
            localEmailDatabase.getEmailDao().delete(email)
        }
    }

    // Must be called on a non-UI thread or Room will throw an exception.
    private fun insertAllToLocalDatabase(emails: Collection<Email?>?) {
        RoomEmailDatabase.databaseExecutorService.execute {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}