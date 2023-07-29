package volovyk.guerrillamail.data.remote

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.model.Email
import volovyk.guerrillamail.data.remote.pojo.CheckForNewEmailsResponse
import volovyk.guerrillamail.data.remote.pojo.GetEmailAddressResponse
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse

class GuerrillaEmailDatabaseTest {

    @Test
    fun setEmailAddressShouldUpdateAssignedEmailAndSidToken() = runTest {
        // Create a mock GuerrillaMailApiInterface
        val guerrillaMailApiInterface = mock<GuerrillaMailApiInterface>()
        val database = GuerrillaEmailDatabase(guerrillaMailApiInterface)
        val requestedEmailAddress = "testytest@guerrillamailblock.com"
        val sidToken = "setEmailAddressShouldUpdateAssignedEmailAddressAndSidToken"

        // Mock the response from the API call
        val response = Response.success(
            SetEmailAddressResponse(requestedEmailAddress, sidToken)
        )
        `when`(
            guerrillaMailApiInterface.setEmailAddress(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Calls.response(response))

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
    fun emailsFlowCollectShouldTryToGetAnEmailAddress() = runTest {
        // Create a mock GuerrillaMailApiInterface
        val guerrillaMailApiInterface = mock<GuerrillaMailApiInterface>()
        val database = GuerrillaEmailDatabase(guerrillaMailApiInterface)

        val emailAddress = "testytest@guerrillamailblock.com"
        val sidToken = "newSidToken"

        // Mock the response from the API call
        val response = Response.success(
            GetEmailAddressResponse(
                emailAddress, sidToken
            )
        )

        `when`(guerrillaMailApiInterface.emailAddress).thenReturn(Calls.response(response))

        database.observeEmails().first()

        // Assert assignedEmail is equal to the one that API returned
        assertEquals(
            emailAddress,
            database.observeAssignedEmail().first()
        )
    }

    @Test
    fun emailsFlowEmitsCorrectEmails() = runTest {
        // Create a mock GuerrillaMailApiInterface
        val guerrillaMailApiInterface = mock<GuerrillaMailApiInterface>()
        val database = GuerrillaEmailDatabase(guerrillaMailApiInterface)

        val emailAddress = "testytest@guerrillamailblock.com"
        val sidToken = "newSidToken"

        // Mock the response from the API call
        val getEmailAddressResponse = Response.success(
            GetEmailAddressResponse(emailAddress, sidToken)
        )

        `when`(guerrillaMailApiInterface.emailAddress).thenReturn(
            Calls.response(
                getEmailAddressResponse
            )
        )

        val remoteEmails = listOf(
            Email("from0", "subject0", "body0", "date0", 0),
            Email("from1", "subject1", "body1", "date1", 1),
            Email("from2", "subject2", "body2", "date2", 2)
        )

        val remoteFullEmails = listOf(
            Email("fromFull0", "subjectFull0", "bodyFull0", "dateFull0", 0),
            Email("fromFull1", "subjectFull1", "bodyFull1", "dateFull1", 1),
            Email("fromFull2", "subjectFull2", "bodyFull2", "dateFull2", 2)
        )

        val checkForNewEmailsResponse = Response.success(
            CheckForNewEmailsResponse(remoteEmails, sidToken)
        )

        `when`(
            guerrillaMailApiInterface.checkForNewEmails(
                anyOrNull(),
                anyInt()
            )
        ).thenReturn(Calls.response(checkForNewEmailsResponse))

        `when`(guerrillaMailApiInterface.fetchEmail(anyOrNull(), eq(0)))
            .thenReturn(Calls.response(remoteFullEmails[0]))
        `when`(guerrillaMailApiInterface.fetchEmail(anyOrNull(), eq(1)))
            .thenReturn(Calls.response(remoteFullEmails[1]))
        `when`(guerrillaMailApiInterface.fetchEmail(anyOrNull(), eq(2)))
            .thenReturn(Calls.response(remoteFullEmails[2]))

        val emittedEmails = database.observeEmails().first()

        // Assert emittedEmails are equal to remote emails
        assertEquals(
            remoteFullEmails,
            emittedEmails
        )
    }
}