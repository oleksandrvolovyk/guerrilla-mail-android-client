package volovyk.guerrillamail.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import volovyk.guerrillamail.data.local.EmailDao
import volovyk.guerrillamail.data.local.LocalEmailDatabase
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.RemoteEmailDatabase

@ExperimentalCoroutinesApi
class EmailRepositoryImplTest {

    @Mock
    private lateinit var remoteEmailDatabase: RemoteEmailDatabase

    @Mock
    private lateinit var localEmailDatabase: LocalEmailDatabase

    @Mock
    private lateinit var emailDao: EmailDao

    private lateinit var emailRepository: EmailRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        `when`(emailDao.all).thenReturn(flow { })
        `when`(remoteEmailDatabase.observeEmails()).thenReturn(flow { })
        `when`(localEmailDatabase.getEmailDao()).thenReturn(emailDao)

        emailRepository = EmailRepositoryImpl(TestScope(), remoteEmailDatabase, localEmailDatabase)
    }

    @Test
    fun setEmailAddressShouldCallRemoteEmailDatabase() =
        runTest {
            val newAddress = "test@example.com"

            emailRepository.setEmailAddress(newAddress)

            verify(remoteEmailDatabase).setEmailAddress(newAddress)
        }

    @Test
    fun deleteEmailShouldCallLocalEmailDatabase() =
        runTest {
            val email = Email("test", "test", "","",1)

            emailRepository.deleteEmail(email)

            verify(localEmailDatabase.getEmailDao()).delete(email)
        }
}