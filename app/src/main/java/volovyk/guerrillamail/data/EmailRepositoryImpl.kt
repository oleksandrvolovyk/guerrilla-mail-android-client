package volovyk.guerrillamail.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailRepositoryImpl @Inject constructor(
    externalScope: CoroutineScope,
    private val remoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase
) : EmailRepository {

    init {
        externalScope.launch {
            withContext(Dispatchers.IO) {
                while (isActive) {
                    if (remoteEmailDatabase.hasEmailAddressAssigned()) {
                        remoteEmailDatabase.updateEmails()
                        delay(REFRESH_INTERVAL)
                    } else {
                        remoteEmailDatabase.getRandomEmailAddress()
                        delay(EMAIL_ASSIGNMENT_INTERVAL)
                    }
                }
            }
        }
        externalScope.launch {
            withContext(Dispatchers.IO) {
                remoteEmailDatabase.observeEmails().collect { emails ->
                    insertAllToLocalDatabase(emails)
                }
            }
        }
    }

    companion object {
        private const val REFRESH_INTERVAL = 5000L // 5 seconds
        private const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts
    }

    override suspend fun getEmailById(emailId: Int): Email? {
        return withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().setEmailViewed(emailId, true)
            localEmailDatabase.getEmailDao().getById(emailId)
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

    override suspend fun deleteAllEmails() {
        withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().deleteAll()
        }
    }

    override fun observeAssignedEmail(): Flow<String?> = remoteEmailDatabase.observeAssignedEmail()
    override fun observeEmails(): Flow<List<Email>> = localEmailDatabase.getEmailDao().all
    override fun observeState(): Flow<RemoteEmailDatabase.State> = remoteEmailDatabase.observeState()

    // Must be called on a non-UI thread or Room will throw an exception.
    private suspend fun insertAllToLocalDatabase(emails: Collection<Email?>?) {
        withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }
    }
}