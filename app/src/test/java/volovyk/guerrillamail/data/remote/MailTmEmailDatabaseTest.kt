package volovyk.guerrillamail.data.remote

import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmApiInterface
import volovyk.guerrillamail.data.emails.remote.mailtm.MailTmEmailDatabase
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.Domain
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.ListOfDomains
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.ListOfMessages
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.LoginResponse
import volovyk.guerrillamail.data.emails.remote.mailtm.entity.Message
import volovyk.guerrillamail.data.emails.remote.model.RemoteEmailDatabaseException
import volovyk.guerrillamail.data.util.Base64EncoderImpl
import java.util.Date

class MailTmEmailDatabaseTest {

    private lateinit var mailTmApiInterface: MailTmApiInterface
    private lateinit var database: MailTmEmailDatabase
    private val base64Encoder = Base64EncoderImpl()

    @Before
    fun setup() {
        mailTmApiInterface = mockk<MailTmApiInterface>()

        database = MailTmEmailDatabase(mailTmApiInterface, base64Encoder)
    }

    @Test(expected = RemoteEmailDatabaseException.NoEmailAddressAssignedException::class)
    fun `updateEmails throws NoEmailAddressAssignedException if called with no email address assigned`(): Unit =
        runTest {
            assertFalse(database.hasEmailAddressAssigned())
            database.updateEmails()
        }

    @Test(expected = RemoteEmailDatabaseException::class)
    fun `getRandomEmailAddress throws RemoteEmailDatabaseException if no domains are available`() =
        runTest {
            // Mock the response from the API call
            val getDomainsResponse = Response.success(ListOfDomains(emptyList(), 0))

            every {
                mailTmApiInterface.getDomains()
            } returns Calls.response(getDomainsResponse)

            database.getRandomEmailAddress()
        }

    @Test
    fun `getRandomEmailAddress makes correct API calls, updates assignedEmail`() = runTest {
        // Mock the response for getDomains()
        val getDomainsResponse =
            Response.success(ListOfDomains(listOf(Domain("id", "example.com")), 1))

        every {
            mailTmApiInterface.getDomains()
        } returns Calls.response(getDomainsResponse)

        // Mock the response for createAccount()
        every {
            mailTmApiInterface.createAccount(any())
        } returns Calls.response(Response.success(Unit))

        // Mock the response for login()
        every {
            mailTmApiInterface.login(any())
        } returns Calls.response(Response.success(LoginResponse("token")))

        database.getRandomEmailAddress()

        verify { mailTmApiInterface.getDomains() }
        verify { mailTmApiInterface.createAccount(any()) }
        verify { mailTmApiInterface.login(any()) }
        assertTrue(
            database.observeAssignedEmail().first()!!.matches(Regex("^\\w+@example\\.com"))
        )
    }

    @Test
    fun `setEmailAddress makes correct API calls, updates assignedEmails`() = runTest {
        val emailAddress = "test@example.com"

        // Mock the response for createAccount()
        every {
            mailTmApiInterface.createAccount(match { it.address == emailAddress })
        } returns Calls.response(Response.success(Unit))

        // Mock the response for login()
        every {
            mailTmApiInterface.login(match { it.address == emailAddress })
        } returns Calls.response(Response.success(LoginResponse("token")))

        database.setEmailAddress(emailAddress)

        verify { mailTmApiInterface.createAccount(match { it.address == emailAddress }) }
        verify { mailTmApiInterface.login(match { it.address == emailAddress }) }
        assertEquals(
            emailAddress,
            database.observeAssignedEmail().first()!!
        )
    }

    @Test
    fun `updateEmails makes correct API calls, updates emails`() = runTest {
        val emailAddress = "test@example.com"

        // Mock static method for base64 encoding
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } returns "encoded"

        // Mock the response for createAccount()
        every {
            mailTmApiInterface.createAccount(match { it.address == emailAddress })
        } returns Calls.response(Response.success(Unit))

        // Mock the response for login()
        every {
            mailTmApiInterface.login(match { it.address == emailAddress })
        } returns Calls.response(Response.success(LoginResponse("token")))

        val messagesList = List(3) { i ->
            Message("id$i", Message.From("address$i", null), null, null, "subject$i", Date())
        }
        val fullMessagesList =
            messagesList.mapIndexed { i, message ->
                message.copy(
                    text = "body$i",
                    html = listOf("html$i")
                )
            }

        val getMessagesResponse = Response.success(
            ListOfMessages(
                messages = messagesList,
                totalMessages = messagesList.size
            )
        )

        // Mock the response for getMessages()
        every {
            mailTmApiInterface.getMessages(token = any())
        } returns Calls.response(getMessagesResponse)

        // Mock responses for each getMessage() and deleteMessage()
        fullMessagesList.forEach { fullMessage ->
            every {
                mailTmApiInterface.getMessage(fullMessage.id, any(), any())
            } returns Calls.response(Response.success(fullMessage))
            every {
                mailTmApiInterface.deleteMessage(fullMessage.id, any())
            } returns Calls.response(Response.success(Unit))
        }

        database.setEmailAddress(emailAddress)
        database.updateEmails()

        val result = database.observeEmails().first()

        assertEquals(
            fullMessagesList.size,
            result.size
        )
        assertEquals(
            fullMessagesList.map { it.text },
            result.map { it.textBody }
        )
    }
}