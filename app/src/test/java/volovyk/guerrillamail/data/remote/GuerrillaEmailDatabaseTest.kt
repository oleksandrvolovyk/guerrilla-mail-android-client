package volovyk.guerrillamail.data.remote

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.guerrillamail.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.remote.guerrillamail.GuerrillaMailApiInterface
import volovyk.guerrillamail.data.remote.guerrillamail.entity.BriefEmail
import volovyk.guerrillamail.data.remote.guerrillamail.entity.CheckForNewEmailsResponse
import volovyk.guerrillamail.data.remote.guerrillamail.entity.GetEmailAddressResponse
import volovyk.guerrillamail.data.remote.guerrillamail.entity.SetEmailAddressResponse

class GuerrillaEmailDatabaseTest {

    private lateinit var guerrillaMailApiInterface: GuerrillaMailApiInterface
    private lateinit var database: GuerrillaEmailDatabase

    @Before
    fun setup() {
        guerrillaMailApiInterface = mockk<GuerrillaMailApiInterface>()
        database = GuerrillaEmailDatabase(guerrillaMailApiInterface)
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

        // Mock the response from the API call
        val getEmailAddressResponse = Response.success(
            GetEmailAddressResponse(emailAddress, sidToken)
        )

        every { guerrillaMailApiInterface.emailAddress } returns Calls.response(
            getEmailAddressResponse
        )

        val remoteEmails = mutableListOf<BriefEmail>()

        repeat(3) { i ->
            remoteEmails.add(BriefEmail(i, "from$i", "subject$i", "date$i"))
        }

        val fullRemoteEmails = remoteEmails.map {
            Email(
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
                    eq(email.id)
                )
            } returns Calls.response(email)
        }

        database.getRandomEmailAddress()
        database.updateEmails()

        val emittedEmails = database.observeEmails().first()

        // Assert emittedEmails are equal to full remote emails
        assertEquals(
            fullRemoteEmails,
            emittedEmails
        )

        // Assert sidToken value is equal to the one returned in CheckForNewEmailsResponse
        assertEquals(
            sidToken,
            database.getSidToken()
        )

        // Assert seq value is equal to the highest email id value
        assertEquals(
            fullRemoteEmails.map { it.id }.maxOf { it },
            database.getSeq()
        )
    }
}