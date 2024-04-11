package volovyk.guerrillamail.data.remote

import android.text.Html
import android.text.SpannedString
import android.util.Base64
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.emails.remote.guerrillamail.GuerrillaMailApiInterface
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.BriefEmail
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.CheckForNewEmailsResponse
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.EmailGuerrillaMail
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.GetEmailAddressResponse
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.SetEmailAddressResponse
import volovyk.guerrillamail.data.emails.remote.guerrillamail.entity.toEmail
import volovyk.guerrillamail.util.AndroidHtmlTextExtractor
import volovyk.guerrillamail.util.Base64EncoderImpl
import volovyk.guerrillamail.util.HtmlTextExtractor

class GuerrillaEmailDatabaseTest {

    private lateinit var guerrillaMailApiInterface: GuerrillaMailApiInterface
    private lateinit var database: GuerrillaEmailDatabase
    private val htmlTextExtractor = AndroidHtmlTextExtractor()
    private val base64Encoder = Base64EncoderImpl()

    @Before
    fun setup() {
        guerrillaMailApiInterface = mockk<GuerrillaMailApiInterface>()
        database =
            GuerrillaEmailDatabase(guerrillaMailApiInterface, htmlTextExtractor, base64Encoder)
    }

    @Test
    fun `setEmailAddress should update assignedEmail and sidToken`() = runTest {
        val requestedEmailAddress = "test@guerrillamailblock.com"
        val sidToken = "new sidToken"

        // Mock the response from the API call
        val response = Response.success(
            SetEmailAddressResponse(requestedEmailAddress, sidToken)
        )
        every {
            guerrillaMailApiInterface.setEmailAddress(
                any(),
                any(),
                any(),
                any()
            )
        } returns Calls.response(response)

        database.setEmailAddress(requestedEmailAddress)

        assertEquals(
            requestedEmailAddress,
            database.observeAssignedEmail().first()
        )

        assertEquals(
            sidToken,
            database.getSidToken()
        )
    }

    @Test
    fun `getRandomEmailAddress should update assignedEmail and sidToken`() = runTest {
        val emailAddress = "test@guerrillamailblock.com"
        val sidToken = "new sidToken"

        // Mock the response from the API call
        val response = Response.success(
            GetEmailAddressResponse(
                emailAddress,
                sidToken
            )
        )

        every { guerrillaMailApiInterface.emailAddress } returns Calls.response(response)

        database.getRandomEmailAddress()

        // Assert assignedEmail is equal to the one that API returned
        assertEquals(
            emailAddress,
            database.observeAssignedEmail().first()
        )

        // Assert sidToken value is equal to the one returned in GetEmailAddressResponse
        assertEquals(
            sidToken,
            database.getSidToken()
        )
    }

    @Test
    fun `updateEmails should emit correct emails, update sidToken and seq values`() = runTest {
        val emailAddress = "test@guerrillamailblock.com"
        val sidToken = "new sidToken"

        // Mock static method for base64 encoding
        mockkStatic(Base64::class)
        every { Base64.encodeToString(any(), any()) } returns "encoded"

        // Mock methods used for html to text conversion
        val spannedStringMock = mockk<SpannedString>()
        every { spannedStringMock.toString() } returns "spanned string"
        mockkStatic(Html::class)
        every { Html.fromHtml(any(), any()) } returns spannedStringMock

        // Mock the response from the API call
        val getEmailAddressResponse = Response.success(
            GetEmailAddressResponse(emailAddress, sidToken)
        )

        every { guerrillaMailApiInterface.emailAddress } returns Calls.response(
            getEmailAddressResponse
        )

        val remoteEmails = mutableListOf<BriefEmail>()

        repeat(3) { i ->
            remoteEmails.add(BriefEmail(i.toString(), "from$i", "subject$i", "date$i"))
        }

        val fullRemoteEmails = remoteEmails.map {
            EmailGuerrillaMail(
                it.id,
                "Full ${it.from}",
                "Full ${it.subject}",
                "Full body ${it.id}",
                "Full ${it.date}"
            )
        }

        val checkForNewEmailsResponse = Response.success(
            CheckForNewEmailsResponse(remoteEmails, sidToken)
        )

        every {
            guerrillaMailApiInterface.checkForNewEmails(
                any(),
                any()
            )
        } returns Calls.response(checkForNewEmailsResponse)

        fullRemoteEmails.forEach { email ->
            every {
                guerrillaMailApiInterface.fetchEmail(
                    any(),
                    eq(email.mailId)
                )
            } returns Calls.response(email)
        }

        database.getRandomEmailAddress()
        database.updateEmails()

        val emittedEmails = database.observeEmails().first()

        // Assert emittedEmails are equal to full remote emails
        assertEquals(
            fullRemoteEmails.map { it.toEmail(htmlTextExtractor, base64Encoder) },
            emittedEmails
        )

        // Assert sidToken value is equal to the one returned in CheckForNewEmailsResponse
        assertEquals(
            sidToken,
            database.getSidToken()
        )

        // Assert seq value is equal to the highest email id value
        assertEquals(
            fullRemoteEmails.map { it.mailId }.maxOf { it },
            database.getSeq().toString()
        )
    }
}