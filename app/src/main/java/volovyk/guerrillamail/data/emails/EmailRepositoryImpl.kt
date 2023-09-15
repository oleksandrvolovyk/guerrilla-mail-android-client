package volovyk.guerrillamail.data.emails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import volovyk.guerrillamail.data.emails.local.LocalEmailDatabase
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import volovyk.guerrillamail.util.State
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmailRepositoryImpl @Inject constructor(
    externalScope: CoroutineScope,
    private val mainRemoteEmailDatabase: RemoteEmailDatabase,
    private val backupRemoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase,
    private val preferencesRepository: PreferencesRepository
) : EmailRepository {

    private var mainRemoteEmailDatabaseIsAvailable = MutableStateFlow(true)

    init {
        externalScope.launch {
            withContext(Dispatchers.IO) {
                mainRemoteEmailDatabaseIsAvailable.update { mainRemoteEmailDatabase.isAvailable() }
            }
        }
        externalScope.launch {
            withContext(Dispatchers.IO) {
                while (isActive) {
                    val remoteEmailDatabase = if (mainRemoteEmailDatabaseIsAvailable.value) {
                        mainRemoteEmailDatabase
                    } else {
                        backupRemoteEmailDatabase
                    }
                    if (remoteEmailDatabase.hasEmailAddressAssigned()) {
                        remoteEmailDatabase.updateEmails()
                        delay(REFRESH_INTERVAL)
                    } else {
                        val lastEmailAddress = preferencesRepository.getValue(LAST_EMAIL_ADDRESS_KEY)
                        if (lastEmailAddress != null && remoteEmailDatabase == mainRemoteEmailDatabase) {
                            remoteEmailDatabase.setEmailAddress(lastEmailAddress)
                        } else {
                            remoteEmailDatabase.getRandomEmailAddress()
                        }
                        delay(EMAIL_ASSIGNMENT_INTERVAL)
                    }
                }
            }
        }
        mainRemoteEmailDatabase.observeEmails().onEach { emails ->
            insertAllToLocalDatabase(emails)
        }.flowOn(Dispatchers.IO).launchIn(externalScope)
        backupRemoteEmailDatabase.observeEmails().onEach {  emails ->
            insertAllToLocalDatabase(emails)
        }.flowOn(Dispatchers.IO).launchIn(externalScope)
    }

    companion object {
        const val REFRESH_INTERVAL = 5000L // 5 seconds
        const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts
        const val LAST_EMAIL_ADDRESS_KEY = "last_email_address"
    }

    override suspend fun getEmailById(emailId: String): Email? {
        return withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().setEmailViewed(emailId, true)
            localEmailDatabase.getEmailDao().getById(emailId)
        }
    }

    override suspend fun setEmailAddress(newAddress: String) {
        withContext(Dispatchers.IO) {
            if (mainRemoteEmailDatabaseIsAvailable.value) {
                mainRemoteEmailDatabase.setEmailAddress(newAddress)
            } else {
                backupRemoteEmailDatabase.setEmailAddress(newAddress)
            }
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

    override suspend fun retryConnectingToMainDatabase() {
        withContext(Dispatchers.IO) {
            mainRemoteEmailDatabaseIsAvailable.update { mainRemoteEmailDatabase.isAvailable() }
        }
    }

    override fun observeAssignedEmail(): Flow<String?> =
        combine(
            mainRemoteEmailDatabase.observeAssignedEmail()
                .onEach {
                    it?.let {
                        preferencesRepository.setValue(LAST_EMAIL_ADDRESS_KEY, it)
                    }
                },
            backupRemoteEmailDatabase.observeAssignedEmail()
        ) { mainEmail, backupEmail ->
            if (mainRemoteEmailDatabaseIsAvailable.value) {
                mainEmail
            } else {
                backupEmail
            }
        }

    override fun observeEmails(): Flow<List<Email>> = localEmailDatabase.getEmailDao().all
    override fun observeState(): Flow<State> =
        combine(
            mainRemoteEmailDatabase.observeState(),
            backupRemoteEmailDatabase.observeState()
        ) { mainState, backupState ->
            if (mainRemoteEmailDatabaseIsAvailable.value) {
                mainState
            } else {
                backupState
            }
        }

    override fun observeMainRemoteEmailDatabaseAvailability(): Flow<Boolean> =
        mainRemoteEmailDatabaseIsAvailable

    // Must be called on a non-UI thread or Room will throw an exception.
    private suspend fun insertAllToLocalDatabase(emails: Collection<Email?>?) {
        withContext(Dispatchers.IO) {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }
    }
}