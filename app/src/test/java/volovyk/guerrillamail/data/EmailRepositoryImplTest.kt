package volovyk.guerrillamail.data

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import volovyk.guerrillamail.data.local.EmailDao
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase

@ExperimentalCoroutinesApi
class EmailRepositoryImplTest {

    private lateinit var remoteEmailDatabase: RemoteEmailDatabase
    private lateinit var localEmailDatabase: LocalEmailDatabase
    private lateinit var emailDao: EmailDao

    private lateinit var emailRepository: EmailRepositoryImpl

    @Before
    fun setup() {
        remoteEmailDatabase = mockk<RemoteEmailDatabase>(relaxed = true)
        localEmailDatabase = mockk<LocalEmailDatabase>(relaxed = true)
        emailDao = mockk<EmailDao>(relaxed = true)

        every { emailDao.all } returns emptyFlow()
        every { remoteEmailDatabase.observeEmails() } returns emptyFlow()
        every { localEmailDatabase.getEmailDao() } returns emailDao

        emailRepository = EmailRepositoryImpl(
            TestScope(),
            remoteEmailDatabase,
            remoteEmailDatabase,
            localEmailDatabase
        )
    }

    @Test
    fun `getEmailById should call LocalEmailDatabase`() = runTest {
        val emailId = 123

        emailRepository.getEmailById(emailId)

        verify { emailDao.getById(emailId) }
    }

    @Test
    fun `setEmailAddress should call RemoteEmailDatabase`() = runTest {
        val newAddress = "test@example.com"

        emailRepository.setEmailAddress(newAddress)

        verify { (remoteEmailDatabase).setEmailAddress(newAddress) }
    }

    @Test
    fun `deleteEmail should call LocalEmailDatabase`() = runTest {
        val email = Email(1, "test", "test", "", "")

        emailRepository.deleteEmail(email)

        verify { emailDao.delete(email) }
    }
}