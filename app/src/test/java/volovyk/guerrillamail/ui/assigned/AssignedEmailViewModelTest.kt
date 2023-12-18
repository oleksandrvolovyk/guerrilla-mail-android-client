package volovyk.guerrillamail.ui.assigned

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import volovyk.MainCoroutineRule
import volovyk.guerrillamail.data.emails.EmailRepository
import volovyk.guerrillamail.util.State

class AssignedEmailViewModelTest {

    private lateinit var viewModel: AssignedEmailViewModel
    private lateinit var emailRepository: EmailRepository

    private lateinit var assignedEmailFlow: MutableStateFlow<String?>
    private lateinit var stateFlow: MutableStateFlow<State>

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        emailRepository = mockk<EmailRepository>(relaxed = true)

        assignedEmailFlow = MutableStateFlow(null)
        stateFlow = MutableStateFlow(State.Loading)

        every { emailRepository.observeAssignedEmail() } returns assignedEmailFlow
        viewModel = AssignedEmailViewModel(emailRepository)
    }

    @Test
    fun `viewModel emits correct default uiState`() = runTest {
        val defaultUiState = AssignedEmailUiState()

        // Assert default UiState
        assertEquals(defaultUiState, viewModel.uiState.value)
    }

    @Test
    fun `viewModel emits correct UiState when an email address is assigned`() = runTest {
        val newEmailAddress = "test@example.com"

        assignedEmailFlow.update { newEmailAddress }

        val expectedUiState = AssignedEmailUiState(
            emailUsername = newEmailAddress.emailUsernamePart(),
            emailDomain = newEmailAddress.emailDomainPart(),
            isGetNewAddressButtonVisible = false
        )

        // Assert new UiState is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)
    }

    @Test
    fun `viewModel processes user changes to email username correctly`() = runTest {
        // When an email address is assigned
        val assignedEmailAddress = "test@example.com"

        assignedEmailFlow.update { assignedEmailAddress }

        val expectedUiState = AssignedEmailUiState(
            emailUsername = assignedEmailAddress.emailUsernamePart(),
            emailDomain = assignedEmailAddress.emailDomainPart(),
            isGetNewAddressButtonVisible = false
        )

        // Assert UiState with assigned email address is emitted
        assertEquals(expectedUiState, viewModel.uiState.value)

        // When user changes email username
        val userEnteredEmailUsername = "test2"

        viewModel.userChangedEmailUsername(userEnteredEmailUsername)

        val expectedUiState2 = AssignedEmailUiState(
            emailUsername = userEnteredEmailUsername,
            emailDomain = assignedEmailAddress.emailDomainPart(),
            isGetNewAddressButtonVisible = true
        )

        // Assert UiState with user entered email username is emitted
        // and "isGetNewAddressButtonVisible" == true
        assertEquals(expectedUiState2, viewModel.uiState.value)

        // When user changes email username to previous one
        viewModel.userChangedEmailUsername(assignedEmailAddress.emailUsernamePart())

        val expectedUiState3 = AssignedEmailUiState(
            emailUsername = assignedEmailAddress.emailUsernamePart(),
            emailDomain = assignedEmailAddress.emailDomainPart(),
            isGetNewAddressButtonVisible = false
        )

        // Assert UiState with "isGetNewAddressButtonVisible" == false is emitted
        assertEquals(expectedUiState3, viewModel.uiState.value)
    }

    @Test
    fun `setEmailAddress calls emailRepository and hides GetNewAddressButton`() = runTest {
        val newAddress = "test@example.com"

        viewModel.userChangedEmailUsername(newAddress.emailUsernamePart())
        assertEquals(true, viewModel.uiState.value.isGetNewAddressButtonVisible)
        viewModel.setEmailAddress(newAddress)

        assertEquals(false, viewModel.uiState.value.isGetNewAddressButtonVisible)
        coVerify { emailRepository.setEmailAddress(newAddress) }
    }

    @Test
    fun `setEmailAddress reverts emailUsername to the last assigned address if a new one cannot be set`() =
        runTest {
            val assignedEmailAddress = "test@example.com"
            val newAddress = "test2@example.com"

            // An email address is assigned
            assignedEmailFlow.update { assignedEmailAddress }

            // Assert UiState shows assigned email
            assertEquals(
                assignedEmailAddress.emailUsernamePart(),
                viewModel.uiState.value.emailUsername
            )
            assertEquals(
                assignedEmailAddress.emailDomainPart(),
                viewModel.uiState.value.emailDomain
            )

            // User changes email username
            viewModel.userChangedEmailUsername(newAddress.emailUsernamePart())

            // Assert new email username is shown
            assertEquals(
                newAddress.emailUsernamePart(),
                viewModel.uiState.value.emailUsername
            )

            // EmailRepository fails to set new email address
            coEvery { emailRepository.setEmailAddress(any()) } returns false

            viewModel.setEmailAddress(newAddress)

            // Assert email username is reverted to last assigned address
            assertEquals(
                assignedEmailAddress.emailUsernamePart(),
                viewModel.uiState.value.emailUsername
            )
        }

    private fun String.emailUsernamePart(): String = this.substringBefore("@")
    private fun String.emailDomainPart(): String = this.substringAfter("@")
}