package volovyk.guerrillamail.ui.assigned

import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
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
        every { emailRepository.observeState() } returns stateFlow
        viewModel = AssignedEmailViewModel(emailRepository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `viewModel emits correct uiState`() = runTest {
        // Create an empty collector for the StateFlow
        val uiStateCollectionJob = backgroundScope.launch(StandardTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        val defaultUiState = AssignedEmailUiState()

        // Assert default UiState
        Assert.assertEquals(defaultUiState, viewModel.uiState.value)

        val newEmailAddress = "test@example.com"

        assignedEmailFlow.update { newEmailAddress }
        stateFlow.update { State.Success }

        advanceUntilIdle()

        val expectedUiState =
            AssignedEmailUiState(assignedEmail = newEmailAddress, state = State.Success)

        // Assert new UiState is emitted
        Assert.assertEquals(expectedUiState, viewModel.uiState.value)

        uiStateCollectionJob.cancel()
    }

    @Test
    fun `setEmailAddress calls emailRepository`() = runTest {
        val newAddress = "test@example.com"

        viewModel.setEmailAddress(newAddress)

        coVerify { emailRepository.setEmailAddress(newAddress) }
    }
}