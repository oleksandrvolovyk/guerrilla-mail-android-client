package volovyk.guerrillamail.data.emails

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
import volovyk.guerrillamail.data.emails.model.EmailRepositoryException
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.emails.remote.model.RemoteEmailDatabaseException
import volovyk.guerrillamail.data.preferences.PreferencesRepository
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    externalScope: CoroutineScope,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val mainRemoteEmailDatabase: RemoteEmailDatabase,
    private val backupRemoteEmailDatabase: RemoteEmailDatabase,
    private val localEmailDatabase: LocalEmailDatabase,
    private val preferencesRepository: PreferencesRepository
) : EmailRepository {

    private val _state = MutableStateFlow(
        EmailRepository.State(
            isLoading = false,
            isMainRemoteEmailDatabaseAvailable = true
        )
    )

    private val _errorsChannel = Channel<EmailRepositoryException>(capacity = Channel.BUFFERED)

    init {
        Timber.d("init ${hashCode()}")
        externalScope.launch(ioDispatcher) {
            _state.updateWithLoading {
                it.copy(
                    isMainRemoteEmailDatabaseAvailable = mainRemoteEmailDatabase.isAvailable()
                )
            }
        }
        externalScope.launch(ioDispatcher) {
            while (isActive) {
                val remoteEmailDatabase = if (_state.value.isMainRemoteEmailDatabaseAvailable) {
                    mainRemoteEmailDatabase
                } else {
                    backupRemoteEmailDatabase
                }
                if (remoteEmailDatabase.hasEmailAddressAssigned()) {
                    _state.withLoading {
                        try {
                            remoteEmailDatabase.updateEmails()
                        } catch (e: RemoteEmailDatabaseException) {
                            _errorsChannel.trySend(EmailRepositoryException.EmailFetchException(e))
                        }
                    }
                    delay(REFRESH_INTERVAL)
                } else {
                    val lastEmailAddress = preferencesRepository.getValue(LAST_EMAIL_ADDRESS_KEY)
                    _state.withLoading {
                        try {
                            if (lastEmailAddress != null && remoteEmailDatabase == mainRemoteEmailDatabase) {
                                remoteEmailDatabase.setEmailAddress(lastEmailAddress)
                            } else {
                                remoteEmailDatabase.getRandomEmailAddress()
                            }
                        } catch (e: RemoteEmailDatabaseException) {
                            _errorsChannel
                                .trySend(EmailRepositoryException.EmailAddressAssignmentException(e))
                        }
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
        return@withContext localEmailDatabase.getEmailDao().getById(emailId)
    }

    override suspend fun setEmailAddress(newAddress: String): String = withContext(ioDispatcher) {
        _state.withLoading {
            try {
                if (_state.value.isMainRemoteEmailDatabaseAvailable) {
                    mainRemoteEmailDatabase.setEmailAddress(newAddress)
                } else {
                    backupRemoteEmailDatabase.setEmailAddress(newAddress)
                }
            } catch (e: RemoteEmailDatabaseException) {
                throw EmailRepositoryException.EmailAddressAssignmentException(e)
            }
        }
    }

    override suspend fun deleteEmails(emailIds: List<String>) = withContext(ioDispatcher) {
        localEmailDatabase.getEmailDao().delete(emailIds)
    }

    override suspend fun retryConnectingToMainDatabase() = withContext(ioDispatcher) {
        _state.updateWithLoading {
            it.copy(
                isMainRemoteEmailDatabaseAvailable = mainRemoteEmailDatabase.isAvailable()
            )
        }
    }

    override fun observeAssignedEmail(): Flow<String?> =
        combine(
            mainRemoteEmailDatabase.observeAssignedEmail().onEach {
                it?.let { preferencesRepository.setValue(LAST_EMAIL_ADDRESS_KEY, it) }
            },
            backupRemoteEmailDatabase.observeAssignedEmail()
        ) { mainEmail, backupEmail ->
            if (_state.value.isMainRemoteEmailDatabaseAvailable) {
                mainEmail
            } else {
                backupEmail
            }
        }

    override fun observeEmails(): Flow<List<Email>> = localEmailDatabase.getEmailDao().all
    override fun observeState(): Flow<EmailRepository.State> = _state.asStateFlow()
    override fun observeErrors(): ReceiveChannel<EmailRepositoryException> = _errorsChannel

    // Must be called on a non-UI thread or Room will throw an exception.
    private suspend fun insertAllToLocalDatabase(emails: Collection<Email>) =
        withContext(ioDispatcher) {
            localEmailDatabase.getEmailDao().insertAll(emails)
        }

    private inline fun MutableStateFlow<EmailRepository.State>.updateWithLoading(
        action: (EmailRepository.State) -> EmailRepository.State
    ) = withLoading { update { action(it) } }

    private inline fun <T> MutableStateFlow<EmailRepository.State>.withLoading(
        action: () -> T
    ): T {
        update { it.copy(isLoading = true) }
        val result = action()
        update { it.copy(isLoading = false) }
        return result
    }
}