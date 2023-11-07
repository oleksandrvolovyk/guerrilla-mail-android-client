package volovyk.guerrillamail.data

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import volovyk.guerrillamail.data.emails.EmailRepositoryImpl
import volovyk.guerrillamail.data.emails.local.EmailDao
import volovyk.guerrillamail.data.emails.local.LocalEmailDatabase
import volovyk.guerrillamail.data.emails.model.Email
import volovyk.guerrillamail.data.emails.remote.RemoteEmailDatabase
import volovyk.guerrillamail.data.preferences.PreferencesRepository

@ExperimentalCoroutinesApi
class EmailRepositoryImplTest {

    private lateinit var mainRemoteEmailDatabase: RemoteEmailDatabase
    private lateinit var backupRemoteEmailDatabase: RemoteEmailDatabase
    private lateinit var localEmailDatabase: LocalEmailDatabase
    private lateinit var emailDao: EmailDao
    private lateinit var preferencesRepository: PreferencesRepository

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var emailRepository: EmailRepositoryImpl

    @Before
    fun setup() {
        mainRemoteEmailDatabase = mockk<RemoteEmailDatabase>(relaxed = true)
        backupRemoteEmailDatabase = mockk<RemoteEmailDatabase>()
        localEmailDatabase = mockk<LocalEmailDatabase>(relaxed = true)
        emailDao = mockk<EmailDao>(relaxed = true)
        preferencesRepository = mockk<PreferencesRepository>()

        coEvery { mainRemoteEmailDatabase.isAvailable() } returns true
        every { emailDao.all } returns emptyFlow()
        every { mainRemoteEmailDatabase.observeEmails() } returns emptyFlow()
        every { backupRemoteEmailDatabase.observeEmails() } returns emptyFlow()
        every { localEmailDatabase.getEmailDao() } returns emailDao
        coEvery {
            preferencesRepository.getValue(EmailRepositoryImpl.LAST_EMAIL_ADDRESS_KEY)
        } returns null
    }

    @Test
    fun `getEmailById should call LocalEmailDatabase`() = testScope.runTest {
        emailRepository = EmailRepositoryImpl(
            testScope,
            testDispatcher,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase,
            preferencesRepository
        )

        val emailId = "123"

        emailRepository.getEmailById(emailId)

        verify { emailDao.getById(emailId) }

        testScope.coroutineContext.cancelChildren()
    }

    @Test
    fun `setEmailAddress should call RemoteEmailDatabase`() = testScope.runTest {
        emailRepository = EmailRepositoryImpl(
            testScope,
            testDispatcher,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase,
            preferencesRepository
        )

        val newAddress = "test@example.com"

        emailRepository.setEmailAddress(newAddress)

        verify { (mainRemoteEmailDatabase).setEmailAddress(newAddress) }

        testScope.coroutineContext.cancelChildren()
    }

    @Test
    fun `deleteEmail should call LocalEmailDatabase`() = testScope.runTest {
        emailRepository = EmailRepositoryImpl(
            testScope,
            testDispatcher,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase,
            preferencesRepository
        )

        val email = Email("1", "test", "test", "", "", "", false)

        emailRepository.deleteEmail(email)

        verify { emailDao.delete(email) }

        testScope.coroutineContext.cancelChildren()
    }

    @Test
    fun `Test emailRepository restores last email address`() = testScope.runTest {
        val email = "test@example.com"

        coEvery {
            preferencesRepository.getValue(EmailRepositoryImpl.LAST_EMAIL_ADDRESS_KEY)
        } returns email

        emailRepository = EmailRepositoryImpl(
            testScope,
            testDispatcher,
            mainRemoteEmailDatabase,
            backupRemoteEmailDatabase,
            localEmailDatabase,
            preferencesRepository
        )

        advanceTimeBy(10_000)

        verify { mainRemoteEmailDatabase.setEmailAddress(email) }

        testScope.coroutineContext.cancelChildren()
    }

    @Test
    fun `Test emailRepository retrieves a random email address when no last address is stored`() =
        testScope.run {
            coEvery {
                preferencesRepository.getValue(EmailRepositoryImpl.LAST_EMAIL_ADDRESS_KEY)
            } returns null

            emailRepository = EmailRepositoryImpl(
                testScope,
                testDispatcher,
                mainRemoteEmailDatabase,
                backupRemoteEmailDatabase,
                localEmailDatabase,
                preferencesRepository
            )

            advanceTimeBy(10_000)

            verify { mainRemoteEmailDatabase.getRandomEmailAddress() }

            testScope.coroutineContext.cancelChildren()
        }
}