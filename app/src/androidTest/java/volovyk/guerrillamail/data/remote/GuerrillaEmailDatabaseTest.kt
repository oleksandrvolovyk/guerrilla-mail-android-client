package volovyk.guerrillamail.data.remote

import androidx.arch.core.executor.testing.CountingTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.anyOrNull
import retrofit2.Response
import retrofit2.mock.Calls
import volovyk.guerrillamail.data.remote.pojo.SetEmailAddressResponse
import java.util.concurrent.TimeUnit


class GuerrillaEmailDatabaseTest {

    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()

    @Test
    fun setEmailAddressShouldUpdateAssignedEmailAndSidToken() {
        // Create a mock ApiInterface
        val apiInterface = mock<ApiInterface>()
        val database = GuerrillaEmailDatabase(apiInterface)
        val requestedEmailAddress = "testytest@guerrillamailblock.com"
        val sidToken = "setEmailAddressShouldUpdateAssignedEmailAddressAndSidToken"

        // Mock the response from the API call
        val response = Response.success(
            SetEmailAddressResponse(
                sidToken,
                requestedEmailAddress
            )
        )
        `when`(
            apiInterface.setEmailAddress(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(Calls.response(response))

        database.setEmailAddress(requestedEmailAddress)

        countingTaskExecutorRule.drainTasks(1, TimeUnit.SECONDS)

        assertEquals(
            requestedEmailAddress,
            database.assignedEmail.value
        )

        assertEquals(
            sidToken,
            database.getSidToken()
        )
    }
}