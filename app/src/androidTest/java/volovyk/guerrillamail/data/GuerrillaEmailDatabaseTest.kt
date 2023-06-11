package volovyk.guerrillamail.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.remote.ApiInterface
import volovyk.guerrillamail.data.remote.GuerrillaEmailDatabase
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse

class GuerrillaEmailDatabaseTest {
    @Test
    fun setEmailAddressShouldUpdateAssignedEmailAddress() {
        // Create a mock ApiInterface
        val apiInterface = mock<ApiInterface>()
        val database = GuerrillaEmailDatabase(apiInterface)
        val requestedEmailAddress = "testytest@guerrillamailblock.com"

        // Mock the response from the API call
        val response = Response.success(SetEmailAddressResponse("sidToken", requestedEmailAddress))
        `when`(
            apiInterface.setEmailAddress(
                anyString(),
                anyString(),
                anyString(),
                anyString()
            )
        ).thenReturn(Calls.response(response))

        // Call the method
        database.setEmailAddress(requestedEmailAddress)

        // Assert the assigned email address
        val assignedEmail = database.assignedEmail.value
        assertEquals(requestedEmailAddress, assignedEmail)
    }
}