package volovyk.guerrillamail.data

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.GuerrillaEmailDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailRepository @Inject constructor(
    private val guerrillaEmailDatabase: GuerrillaEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase
) : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry
    val assignedEmail: LiveData<String?>?
    val emails: Flow<List<Email>>
    val refreshing: LiveData<Boolean>
    val errorLiveData: LiveData<SingleEvent<String>>

    init {
        assignedEmail = guerrillaEmailDatabase.assignedEmail
        emails = localEmailDatabase.emailDao().all
        val remoteEmails = guerrillaEmailDatabase.emails
        refreshing = guerrillaEmailDatabase.refreshing
        errorLiveData = guerrillaEmailDatabase.errorLiveData
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
        guerrillaEmailDatabase.setEmailAddress(newAddress)
    }

    fun deleteEmail(email: Email?) {
        LocalEmailDatabase.databaseExecutorService.execute {
            localEmailDatabase.emailDao().delete(email)
        }
    }

    // Must be called on a non-UI thread or Room will throw an exception.
    private fun insertAllToLocalDatabase(emails: Collection<Email?>?) {
        LocalEmailDatabase.databaseExecutorService.execute {
            localEmailDatabase.emailDao().insertAll(emails)
        }
    }

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}