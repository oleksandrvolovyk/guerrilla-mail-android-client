package volovyk.guerrillamail.data.emails

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
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
import timber.log.Timber
import volovyk.guerrillamail.data.IoDispatcher
import volovyk.guerrillamail.data.emails.local.LocalEmailDatabase
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import volovyk.guerrillamail.util.State
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val mainRemoteEmailDatabase: RemoteEmailDatabase,
    private val backupRemoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase,
    private val preferencesRepository: PreferencesRepository
) : EmailRepository {

    private var mainRemoteEmailDatabaseIsAvailable = MutableStateFlow(true)

    init {
        Timber.d("init ${hashCode()}")
        externalScope.launch(ioDispatcher) {
            mainRemoteEmailDatabaseIsAvailable.update { mainRemoteEmailDatabase.isAvailable() }
        }
        externalScope.launch(ioDispatcher) {
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
        mainRemoteEmailDatabase.observeEmails().onEach { emails ->
            insertAllToLocalDatabase(emails)
        }.flowOn(ioDispatcher).launchIn(externalScope)
        backupRemoteEmailDatabase.observeEmails().onEach { emails ->
            insertAllToLocalDatabase(emails)
        }.flowOn(ioDispatcher).launchIn(externalScope)
    }

    companion object {
        const val REFRESH_INTERVAL = 5000L // 5 seconds
        const val EMAIL_ASSIGNMENT_INTERVAL = 1000L // 1 second, interval between attempts
        const val LAST_EMAIL_ADDRESS_KEY = "last_email_address"
    }

    override suspend fun getEmailById(emailId: String): Email? = withContext(ioDispatcher) {
        localEmailDatabase.getEmailDao().setEmailViewed(emailId, true)
        localEmailDatabase.getEmailDao().getById(emailId)
    }

    override suspend fun setEmailAddress(newAddress: String): Boolean = withContext(ioDispatcher) {
        if (mainRemoteEmailDatabaseIsAvailable.value) {
            mainRemoteEmailDatabase.setEmailAddress(newAddress)
        } else {
            backupRemoteEmailDatabase.setEmailAddress(newAddress)
        }
    }

    override suspend fun deleteEmails(emailIds: List<String>) = withContext(ioDispatcher) {
        localEmailDatabase.getEmailDao().delete(emailIds)
    }

    override suspend fun retryConnectingToMainDatabase() = withContext(ioDispatcher) {
        mainRemoteEmailDatabaseIsAvailable.update { mainRemoteEmailDatabase.isAvailable() }
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
    private suspend fun insertAllToLocalDatabase(emails: Collection<Email>) =
        withContext(ioDispatcher) {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }
}